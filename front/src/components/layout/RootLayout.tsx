import React, { useState, useEffect } from 'react';
import axios from "axios";
import { REACT_APP_HOST } from "../../common/configData";
import { toast } from "react-toastify";
import * as types from "../../common/types/User"
import CustomToastContainer from "../util/CustomToastContainer";
import { RootLayoutContainer, Header, Main, Section, RealEstateList, RealEstateCard, Pagination, Footer } from "./RootLayout.style";

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

    useEffect(() => {
        getRealEstate();
    }, []);

    async function getRealEstate() {
        try{
            // const res = await axios.get('http://' + REACT_APP_HOST + '/api/realestate', {otherID : userInfo.id}, {withCredentials: true})
            const res = await axios.get<types.RealEstate[]>('http://' + REACT_APP_HOST + '/api/realestate', {withCredentials: true});
            setRealEstates(res.data);
            setSearchResults(res.data); // 처음에는 모든 매물을 보여줌
            toast.success('정보 불러오기 성공');
            
        }catch(err: any){
            toast.error(err.response.data.message);
            
        }
    };

    function handleSearch(e: any) {
        e.preventDefault();
        
        if (searchTerm === "") {
            setSearchResults(realEstates); // 검색어가 없으면 모든 매물을 보여줌
        } else {
            const results = realEstates.filter(
            (realEstate) =>
                realEstate.title.toLowerCase().includes(searchTerm.toLowerCase())
            );
            setSearchResults(results);
        }
        setCurrentPage(1); // 검색 시 현재 페이지를 1로 초기화
    }

    function handlePageChange(pageNumber: number) {
        setCurrentPage(pageNumber);
    }
    
    function getCurrentPageResults(): types.RealEstate[] {
        const startIndex = (currentPage - 1) * pageSize;
        const endIndex = startIndex + pageSize;
        return searchResults.slice(startIndex, endIndex);
    }

    return (
        <>
      {/* <CustomToastContainer/> */}
      <header>
        <h1>부동산 거래 사이트</h1>
      </header>
      <main>
        <section>
          <h2>인기 매물</h2>
          <div className="real-estate-list">
            {getCurrentPageResults().map(realEstate => (
              <div className="real-estate-card" key={realEstate.id}>
                <img src={realEstate.image} alt={realEstate.title} />
                <h3>{realEstate.title}</h3>
                <p>{realEstate.description}</p>
                <p>가격: {realEstate.price}</p>
              </div>
            ))}
          </div>
          {/* 페이지네이션 컴포넌트 */}
          <div className="pagination">
            {Array.from({ length: Math.ceil(searchResults.length / pageSize) }, (_, index) => (
              <button
                key={index + 1}
                onClick={() => handlePageChange(index + 1)}
                className={currentPage === index + 1 ? 'active' : ''}
              >
                {index + 1}
              </button>
            ))}
          </div>
        </section>
        <section>
          <h2>부동산 검색</h2>
          <form onSubmit={handleSearch}>
            <input
              type="text"
              placeholder="검색어를 입력하세요"
              value={searchTerm}
              onChange={e => setSearchTerm(e.target.value)}
            />
            <button type="submit">검색</button>
          </form>
        </section>
      </main>
      <footer>
        <p>부동산 거래 사이트에 오신 것을 환영합니다.</p>
      </footer>
    </>
    );
}

export default RootLayout;