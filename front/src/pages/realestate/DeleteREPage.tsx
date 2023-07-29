import React, { useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import axios from 'axios';
import * as types from "../../common/types/User";
import { toast } from "react-toastify";

interface RouteParams {
  id: string;
}

function DeleteREPage() {
  const { id } = useParams<string>();
  const navigate = useNavigate();

  useEffect(() => {
    deleteRealEstate();
  }, []);

  const deleteRealEstate = async () => {
    try {
      await axios.delete(`/api/realestate/${id}`);
    } catch (error: any) {
      
    } finally {
      navigate('/realestate'); // 매물 목록 페이지로 리다이렉트
    }
  };

  return (
    <div>
      <h2>부동산 매물 삭제</h2>
      <p>정말로 매물을 삭제하시겠습니까?</p>
    </div>
  );
}

export default DeleteREPage;