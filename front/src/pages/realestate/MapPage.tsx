import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';
import * as types from "../../common/types/User";
import { toast } from "react-toastify";
import { Map, MapMarker, ZoomControl } from "react-kakao-maps-sdk";
import { REACT_APP_NAME, REACT_APP_MY_LOCATE_X, REACT_APP_MY_LOCATE_Y } from '../../common/configData';
import { useSetRecoilState , useRecoilValue, } from "recoil";
import { realestateModalState, mainUpdateChecker, realestateFilterState, } from "../../common/states/recoilModalState";

function MapPage() {
  const [realEstates, setRealEstates] = useState<types.RealEstate[]>([]);
  const [searchTerm, setSearchTerm] = useState<string>("");
  const [searchResults, setSearchResults] = useState<types.RealEstate[]>([]);
  const filterState = useRecoilValue(realestateFilterState);
  const setModalState = useSetRecoilState(realestateModalState);

  useEffect(() => {
    const reloadPage = async () => {
      try {
        await getRealEstate();
      } catch (error) {
      
      }
    }
    reloadPage();
  }, []);

  useEffect(() => {
    rearrangeByFilter();
  }, [filterState]);

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
  }

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
  }

  function handleClick(realEstateId: number) {
    setModalState({ realestateId: realEstateId, show: true });
  }

  return (
    <main style={{ width: "100%", height: "100%", }}>
    <section style={{ width: "100%", }}>
      <form onSubmit={handleSearch} style={{ width: "100%", }}>
        <input
          type="text"
          placeholder="매물 검색"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        <button type="submit">검색</button>
      </form>
    </section>
    <section style={{ width: "100%", height: "90vh" }}>
    <Map
      className="myMap"
      style={{ width: "100%", height: "100%", }}
      center={{ lat: Number(REACT_APP_MY_LOCATE_Y), lng: Number(REACT_APP_MY_LOCATE_X) }}
      level={6}
    >
      <ZoomControl />
    <MapMarker position={{ lat: Number(REACT_APP_MY_LOCATE_Y), lng: Number(REACT_APP_MY_LOCATE_X) }}>
      <div style={{textAlign:"center", width:"15vh"}}>{REACT_APP_NAME}</div>
    </MapMarker>
    {searchResults
      .map((realEstate, i) => (
        realEstate.latitude && realEstate.longitude &&
        <MapMarker
          position={{ lat: realEstate.latitude, lng: realEstate.longitude }}
          onClick={() => handleClick(realEstate.id)}
        >
          <div
            style={{textAlign:"center", width:"15vh"}}
            onClick={() => handleClick(realEstate.id)}
          >
            {realEstate.title}
          </div>
        </MapMarker>
      ))}
    </Map>
    </section>
    </main>
  );
}

export default MapPage;