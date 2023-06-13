import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import * as types from "../../common/types/User";
import { toast } from "react-toastify";

interface RouteParams {
  id: string;
}

function PatchREPage() {
  const { id } = useParams<string>();
  const [title, setTitle] = useState<string>('');
  const [description, setDescription] = useState<string>('');
  const [price, setPrice] = useState<number>(0);

  useEffect(() => {
    getRealEstate();
  }, []);

  const getRealEstate = async () => {
    try {
      const response = await axios.get<types.RealEstate>(`/api/realestate/${id}`);
      const { title, description, price } = response.data;
      setTitle(title);
      setDescription(description);
      setPrice(price);
    } catch (error: any) {
      console.error(error.response.data); // 에러 처리 로직 추가
    }
  };

  const handleFormSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      const response = await axios.put<types.RealEstate>(`/api/realestate/${id}`, {
        title,
        description,
        price,
      });

      console.log(response.data); // 매물 수정 성공 시 서버 응답 출력
      // 성공 메시지 또는 리다이렉트 등의 추가 작업 수행
    } catch (error: any) {
      console.error(error.response.data); // 매물 수정 실패 시 에러 메시지 출력
      // 실패 메시지 또는 오류 처리 등의 추가 작업 수행
    }
  };

  return (
    <div>
      <h2>부동산 매물 수정</h2>
      <form onSubmit={handleFormSubmit}>
        <div>
          <label htmlFor="title">제목</label>
          <input
            type="text"
            id="title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
          />
        </div>
        <div>
          <label htmlFor="description">설명</label>
          <textarea
            id="description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            required
          ></textarea>
        </div>
        <div>
          <label htmlFor="price">가격</label>
          <input
            type="number"
            id="price"
            value={price}
            onChange={(e) => setPrice(Number(e.target.value))}
            required
          />
        </div>
        <button type="submit">수정</button>
      </form>
    </div>
  );
}

export default PatchREPage;