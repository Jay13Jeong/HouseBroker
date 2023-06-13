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
      console.log('매물 삭제 성공'); // 삭제 성공 시 메시지 또는 리다이렉트 등의 추가 작업 수행
    } catch (error: any) {
      console.error(error.response.data); // 삭제 실패 시 에러 메시지 출력
      // 실패 메시지 또는 오류 처리 등의 추가 작업 수행
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