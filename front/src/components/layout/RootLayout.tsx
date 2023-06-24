import React, { useState, useEffect } from 'react';
import axios from "axios";
import { REACT_APP_HOST } from "../../common/configData";
import { toast } from "react-toastify";
import * as types from "../../common/types/User";
import CustomToastContainer from "../util/CustomToastContainer";
import { useSetRecoilState , useResetRecoilState, useRecoilValue } from "recoil"
import { realestateModalState } from "../../common/states/recoilModalState";

function RootLayout() {
  const [realEstates, setRealEstates] = useState<types.RealEstate[]>([
    {
      id: 1,
      title: '아파트 A',
      description: '아파트 A의 설명',
      price: 100000,
      image: '아파트 A의 이미지 URL',
    },
    {
      id: 2,
      title: '주택 B',
      description: '주택 B의 설명',
      price: 200000,
      image: '주택 B의 이미지 URL',
    },
    {
      id: 3,
      title: '상가 C',
      description: '상가 C의 설명',
      price: 300000,
      image: '상가 C의 이미지 URL',
    },
  ]);
  const [searchTerm, setSearchTerm] = useState<string>("");
  const [searchResults, setSearchResults] = useState<types.RealEstate[]>([]);
  const [currentPage, setCurrentPage] = useState<number>(1);
  const [pageSize, setPageSize] = useState<number>(10);
  const showModal = useRecoilValue(realestateModalState);
  const setModalState = useSetRecoilState(realestateModalState);

  useEffect(() => {
    getRealEstate();
  }, []);

  async function getRealEstate() {
    try {
      const res = await axios.get<types.RealEstate[]>('/api/realestate/', { withCredentials: true });
      console.log(res.data);
      setRealEstates(res.data);
      setSearchResults(res.data);
      toast.success('정보 불러오기 성공');
    } catch (err: any) {
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
    // console.log("remonn")
    // console.log(realEstateId)
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

  return (
    <>
      <main>
        <section>
          <form onSubmit={handleSearch}>
            <input
              type="text"
              placeholder="매물 검색"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
            <button type="submit">검색</button>
          </form>
        </section>
        <br/>
        <section>
          <table className="real-estate-table">
            <tbody>
              {Array.from(
                { length: Math.ceil(searchResults.length / 6) },
                (_, index) => (
                  <tr key={index}>
                    {getCurrentPageResults()
                      .slice(index * 6, index * 6 + 6)
                      .map((realEstate) => (
                        <td 
                            key={realEstate.id}
                            className="real-estate-card"
                            onClick={() => handleClick(realEstate.id)}
                        >
                          <img src={realEstate.image} alt={realEstate.title} />
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
        </section>
        <br/>
        <section>
          <form onSubmit={handleSearch}>
            <input
              type="text"
              placeholder="매물 검색"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
            <button type="submit">검색</button>
          </form>
        </section>
      </main>
    </>
  );
}

export default RootLayout;