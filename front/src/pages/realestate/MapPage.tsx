import React, { useEffect, useState } from 'react';
import axios from 'axios';
import * as types from "../../common/types/User";
import { toast } from "react-toastify";
import { Map, MapMarker, ZoomControl } from "react-kakao-maps-sdk";
import { REACT_APP_NAME, REACT_APP_MY_LOCATE_X, REACT_APP_MY_LOCATE_Y } from '../../common/configData';
import { useSetRecoilState , useRecoilValue, } from "recoil";
import { realestateModalState, mainUpdateChecker, realestateFilterState, } from "../../common/states/recoilModalState";
import { TextField } from '@mui/material';
import { DefaultButton } from '../../components/common';

const defaultLocate = { lat: Number(REACT_APP_MY_LOCATE_Y), lng: Number(REACT_APP_MY_LOCATE_X) }
const defaultLocate2 = { lat: Number(REACT_APP_MY_LOCATE_Y), lng: Number(REACT_APP_MY_LOCATE_X) + 0.0000000001 }

function MapPage() {
  const [realEstates, setRealEstates] = useState<types.RealEstate[]>([]);
  const [searchTerm, setSearchTerm] = useState<string>("");
  const [searchResults, setSearchResults] = useState<types.RealEstate[]>([]);
  const filterState = useRecoilValue(realestateFilterState);
  const setModalState = useSetRecoilState(realestateModalState);
  const [mapCenter, setMapCenter] = useState<{ lat: number; lng: number }>(defaultLocate2);

  useEffect(() => {
    const reloadPage = async () => {
      try {
        await getRealEstate();
        setMapCenter(defaultLocate);
      } catch (error) { }
    }
    reloadPage();
    setMapCenter((preState) => {
      if (preState === defaultLocate)
        return defaultLocate2;
      return defaultLocate;
    })
  }, []);

  useEffect(() => {
    rearrangeByFilter();
    setMapCenter((preState) => {
      if (preState === defaultLocate)
        return defaultLocate2;
      return defaultLocate;
    })
  }, [filterState]);

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

  const handleMapMove = (e: any) => {
    if (e.key === 'Enter' && e.keyCode !== 13) return;
    if (searchResults.length > 0 && searchResults[0].latitude && searchResults[0].longitude)
      setMapCenter({lat: searchResults[0].latitude, lng: searchResults[0].longitude});
    else{
      setMapCenter((preState) => {
        if (preState === defaultLocate)
          return defaultLocate2;
        return defaultLocate;
      })
      toast.info("검색 결과가 없습니다");
    } 
  }

  function handleClick(realEstateId: number) {
    setModalState({ realestateId: realEstateId, show: true });
  }

  return (
    <main style={{ width: "100%", height: "100%", }}>
    <section style={{ width: "100%", }}>
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
          if (e.key !== 'Enter') return;
          handleMapMove(e)
        }}
      />
      <DefaultButton
        onClick={handleMapMove}
        sx={{ width: "20%", marginTop: 0 }}
      >
        검색
      </DefaultButton>
    </section>
    <section style={{ width: "100%", height: "90vh" }}>
    <Map
      className="myMap"
      style={{ width: "100%", height: "90vh", }}
      center={mapCenter}
      level={6}
    >
      <ZoomControl />
      {searchResults
        .map((realEstate) => (
          realEstate.latitude && realEstate.longitude &&
          <MapMarker
            key={realEstate.id}
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
      <MapMarker position={{ lat: Number(REACT_APP_MY_LOCATE_Y), lng: Number(REACT_APP_MY_LOCATE_X) }}>
        <div style={{textAlign:"center", width:"15vh"}}>{REACT_APP_NAME}</div>
      </MapMarker>
    </Map>
    </section>
    </main>
  );
}

export default MapPage;