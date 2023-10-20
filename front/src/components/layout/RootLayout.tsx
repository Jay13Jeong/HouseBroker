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
  socketIdState,
} from "../../common/states/recoilModalState";
import { Avatar, Button, TextField } from '@mui/material';
import { useSocket } from '../../common/states/socketContext';
import { CardSection, Main, SearchSection } from './RootLayout.style';
import { DefaultButton } from '../common';


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
  const setSocketIdState = useSetRecoilState(socketIdState);
  // const socketId = useRecoilValue(socketIdState);
 
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
    // toast.info("socket server accessed");
    socket.addSubscribe('/topic/hi', (message: any) => {
      // toast.success("recv socket : " +  message.headers['message-id']);
      socket.unsubscribe('/topic/hi');
      const sId = "-user" + (message.headers['message-id'].split('-')[0]);
      setSocketIdState( {socketId : sId} );
      socket.addSubscribe('/topic/refresh' + sId, () => {
        // alert("!!!");
        window.location.reload();
      });
      //////////////
      // socket.addSubscribe('/topic/message3' + sId, (message) => {
      //   toast.success("chat : " +  message.body);
      // });
      /////////////////
    });
    socket.sendMessage('/app/hello', '');
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
      const realEstates = getCurrentPageResults();
      for (const realEstate of realEstates) {
        if (!realEstate.imageSlotState || !realEstate.imageSlotState.includes(1)){
          imgData = require("../../assets/sampleroom.png");
        } else {
          imgData = await getImageData(realEstate.id);
        }
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

  //////이미지 합성////////////
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
  
      // 화면 비율의 30%를 넘지 않도록 최대 크기 설정
      const maxScreenWidth = window.innerWidth * 0.3;
      const maxScreenHeight = window.innerHeight * 0.3;
  
      // 베이스 이미지와 오버레이 이미지의 가로, 세로 비율 계산
      const baseAspect = baseImg.width / baseImg.height;
      const overlayAspect = overlayImg.width / overlayImg.height;
  
      // 베이스 이미지 크기 계산
      let baseWidth = baseImg.width;
      let baseHeight = baseImg.height;
  
      if (baseWidth > maxScreenWidth) {
        baseWidth = maxScreenWidth;
        baseHeight = maxScreenWidth / baseAspect;
      }
      if (baseHeight > maxScreenHeight) {
        baseHeight = maxScreenHeight;
        baseWidth = maxScreenHeight * baseAspect;
      }
  
      // 오버레이 이미지 크기 계산
      let overlayWidth = overlayImg.width;
      let overlayHeight = overlayImg.height;
  
      if (overlayWidth > baseWidth) {
        overlayWidth = baseWidth;
        overlayHeight = baseWidth / overlayAspect;
      }
      if (overlayHeight > baseHeight) {
        overlayHeight = baseHeight;
        overlayWidth = baseHeight * overlayAspect;
      }
  
      // 캔버스 크기 설정
      canvas.width = baseWidth;
      canvas.height = baseHeight;
  
      const context: CanvasRenderingContext2D | null = canvas.getContext('2d');
      if (context === null) {
        return overlayImgUrl;
      }
  
      // 오버레이 이미지를 중앙에 배치
      const xOffset = (canvas.width - overlayWidth) / 2;
      const yOffset = (canvas.height - overlayHeight) / 2;
  
      context.drawImage(baseImg, 0, 0, canvas.width, canvas.height);
      context.drawImage(overlayImg, xOffset, yOffset, overlayWidth, overlayHeight);
  
      const composedImgUrl = canvas.toDataURL();
      return composedImgUrl;
    } catch (error) {
      return overlayImgUrl;
    }
  };
  ////이미지 합성끝/////////////

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
  
  const handleEmptyCheck = (e: any) => { 
    if (e.key === 'Enter' && e.keyCode !== 13) return;
    if (searchResults.length === 0){
      toast.info("검색 결과가 없습니다");
    }
    /////////////////
    // socket.sendMessage('/app/send2/1', 'Hello client?');
    // socket.sendMessage('/app/logout', 't?');
    /////////////
  }

  return (
      <Main>
        <SearchSection>
          <TextField
            label="매물검색"
            variant="outlined"
            size="small"
            sx={{ width: "75%" }}
            onChange={(e) => {
              setSearchTerm(e.target.value);
              handleSearch(e);
            }}
            onKeyDown={(e) => {
              if (e.key === 'Enter'){
                handleEmptyCheck(e);  
              }
            }}
          />
          <DefaultButton
            onClick={handleEmptyCheck}
            sx={{ width: "20%", marginTop: 0, marginBottom: 0 }}
          >
            검색
          </DefaultButton>
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
                          <Avatar src={estateImgs[index * 4 + i]} alt="estate_image" variant="rounded" sx={{ width: "100%", height: 250 }} onDragStart={e => e.preventDefault()}/>
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
          <center className="pagination">
            {Array.from(
              { length: Math.ceil(searchResults.length / pageSize) },
              (_, index) => (
                <DefaultButton
                  key={index + 1}
                  onClick={() => handlePageChange(index + 1)}
                  className={(currentPage === index + 1 ? 'active' : '') + " pageBtn"}
                >
                  {index + 1}
                </DefaultButton>
              )
            )}
          </center>
        </CardSection>
        <br/>
      </Main>
  );
}

export default RootLayout;