import React, { useState, useEffect, useContext, } from 'react';
import axios from "axios";
import { toast } from "react-toastify";
import * as types from "../../common/types/User";
import { useSetRecoilState , useRecoilValue, } from "recoil";
import { 
  realestateModalState, 
  mainUpdateChecker, 
  realestateFilterState,
  socketConnectState,
} from "../../common/states/recoilModalState";
import { Avatar } from '@mui/material';
import { useSocket } from '../../common/states/socketContext';
import { CardSection, Main, SearchSection } from './RootLayout.style';


function RootLayout() {
  const [realEstates, setRealEstates] = useState<types.RealEstate[]>([]);
  const [searchTerm, setSearchTerm] = useState<string>("");
  const [searchResults, setSearchResults] = useState<types.RealEstate[]>([]);
  const [currentPage, setCurrentPage] = useState<number>(1);
  const [pageSize, setPageSize] = useState<number>(8);
  const filterState = useRecoilValue(realestateFilterState);
  const setModalState = useSetRecoilState(realestateModalState);
  const updateChecker = useRecoilValue(mainUpdateChecker);
  const [estateImgs, setEstateImgs] = useState<string[]>([]);
  const socketState = useRecoilValue(socketConnectState);
  const socket = useSocket();
 
  useEffect(() => {
    const reloadPage = async () => {
      try {
        await getRealEstate();
      } catch (error) {
      
      }
    }
    reloadPage();
  }, [updateChecker]);

  useEffect(() => {
    if (!socket.stomp.connected)
      return;
    toast.info("socket server accessed");
    socket.addSubscribe('/topic/hi', (message) => {
    toast.success("recv socket : " +  message.body);
    socket.unsubscribe('/topic/hi');
    });
    socket.sendMessage('/app/hello', 'Hello client?');
  }, [socketState]);

  useEffect(() => {
    get8Imgs();
  }, [searchResults,currentPage]);

  useEffect(() => {
    rearrangeByFilter();
  }, [filterState]);

  const fetchSampleImgs = () => {
    setEstateImgs([]);
    for (let i = 0; i < 8; i++){
      setEstateImgs((imgs) => [
        ...imgs,
        require("../../assets/sampleroom.png")
      ])
    }
  }

  const get8Imgs = async () => {
    fetchSampleImgs();
    try{
      let new8Imgs : string[] = [];
      let imgData = "";
      for (const realEstate of getCurrentPageResults()) {
        imgData = await getImageData(realEstate.id);
        if (realEstate.soldout === true)
          imgData = await composeImages(imgData, require("../../assets/SOLD_OUT.png"))
        new8Imgs = [...new8Imgs, imgData];
      }
      setEstateImgs((prevImgs) => ([...new8Imgs, ...prevImgs]))
    } catch (errer) {
      toast.error("이미지 불러오기 실패");
    }
  }

  async function getRealEstate() {
    try {
      const res = await axios.get<types.RealEstate[]>('/api/realestate/', { withCredentials: true });
      // console.log(res.data);
      setRealEstates(res.data);
      setSearchResults(res.data);
    } catch (err: any) {
      toast.error('정보 불러오기 실패');
      toast.error(err.response.data.message);
    }
  };

  function handleSearch(e: React.FormEvent) {
    e.preventDefault();

    if (searchTerm === "") {
      setSearchResults(realEstates);
    } else {
      const results = realEstates.filter(
        (realEstate) =>
          realEstate.title.toLowerCase().includes(searchTerm.toLowerCase())
      );
      setSearchResults(results);
    }
    setCurrentPage(1);
  }

  function handlePageChange(pageNumber: number) {
    setCurrentPage(pageNumber);
  }

  function handleClick(realEstateId: number) {
    setModalState({ realestateId: realEstateId, show: true });
  }

  function getCurrentPageResults(): types.RealEstate[] {
    const startIndex = (currentPage - 1) * pageSize;
    const endIndex = startIndex + pageSize;
    return searchResults.slice(startIndex, endIndex);
  }

  function truncateTitle(title: string, maxLength: number): string {
    if (title.length > maxLength) {
      return title.substring(0, maxLength - 3) + '...';
    }
    return title;
  }

  function formatPrice(price: number): string {
    const maxPrice = 2100000000;
    if (price > maxPrice) {
      return maxPrice.toLocaleString();
    }
    return price.toLocaleString();
  }

  const getImageData = async (id: number) => {
    try {
      const imgDataRes = await axios.get('/api/realestate/image/' + id + '/1', {
        withCredentials: true,
        responseType: 'blob'
      });
      return URL.createObjectURL(imgDataRes.data);
    } catch (error) {
      return require("../../assets/sampleroom.png");
    }
  };

  const composeImages = async (baseImgUrl: string, overlayImgUrl: string): Promise<string> => {
    const baseImgPromise = new Promise<HTMLImageElement>((resolve, reject) => {
      const baseImg = new Image();
      baseImg.crossOrigin = 'Anonymous';
      baseImg.onload = function () {
        resolve(baseImg);
      };
      baseImg.onerror = function () {
        reject();
      };
      baseImg.src = baseImgUrl;
    });
  
    const overlayImgPromise = new Promise<HTMLImageElement>((resolve, reject) => {
      const overlayImg = new Image();
      overlayImg.crossOrigin = 'Anonymous';
      overlayImg.onload = function () {
        resolve(overlayImg);
      };
      overlayImg.onerror = function () {
        reject();
      };
      overlayImg.src = overlayImgUrl;
    });
  
    try {
      const [baseImg, overlayImg] = await Promise.all([baseImgPromise, overlayImgPromise]);
  
      const canvas = document.createElement('canvas');
      canvas.width = baseImg.width;
      canvas.height = baseImg.height;
  
      const context: CanvasRenderingContext2D | null = canvas.getContext('2d');
      if (context === null) {
        return overlayImgUrl;
      }
  
      context.drawImage(baseImg, 0, 0);
      context.drawImage(overlayImg, 0, 0, baseImg.width, baseImg.height);
  
      const composedImgUrl = canvas.toDataURL();
      return composedImgUrl;
    } catch (error) {
      return overlayImgUrl;
    }
  };

  const rearrangeByFilter = () => {
    if (filterState.filter === "default"){
      setSearchResults(realEstates);  
    } else {
      const results = realEstates.filter(
        (realEstate) => {
          if (realEstate.relay_object_type === null)
            return false;
          return realEstate.relay_object_type.includes(filterState.filter)
        }
      );
      setSearchResults(results);
    }
    setCurrentPage(1);
  }

  return (
      <Main>
        <SearchSection>
          <form onSubmit={handleSearch}>
            <input
              type="text"
              placeholder="매물 검색"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
            <button type="submit">검색</button>
          </form>
        </SearchSection>
        <br/>
        <CardSection>
          <table className="real-estate-table">
            <tbody>
              {Array.from(
                { length: Math.ceil(searchResults.length / 4) },
                (_, index) => (
                  <tr key={index}>
                    {getCurrentPageResults()
                      .slice(index * 4, index * 4 + 4)
                      .map((realEstate, i) => (
                        <td 
                            key={realEstate.id}
                            className="real-estate-card"
                            onClick={() => handleClick(realEstate.id)}
                        >
                          <Avatar src={estateImgs[index * 4 + i]} alt="estate_image" variant="rounded" sx={{ width: "100%", height: 250 }} />
                          <h3>{truncateTitle(realEstate.title, 20)}</h3>
                          <p>{truncateTitle(realEstate.description, 20)}</p>
                          <p>가격: {formatPrice(realEstate.price)}</p>
                        </td>
                      ))}
                  </tr>
                )
              )}
            </tbody>
          </table>
          <div className="pagination">
            {Array.from(
              { length: Math.ceil(searchResults.length / pageSize) },
              (_, index) => (
                <button
                  key={index + 1}
                  onClick={() => handlePageChange(index + 1)}
                  className={currentPage === index + 1 ? 'active' : ''}
                >
                  {index + 1}
                </button>
              )
            )}
          </div>
        </CardSection>
        <br/>
        <SearchSection>
          <form onSubmit={handleSearch}>
            <input
              type="text"
              placeholder="매물 검색"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
            <button type="submit">검색</button>
          </form>
        </SearchSection>
      </Main>
  );
}

export default RootLayout;