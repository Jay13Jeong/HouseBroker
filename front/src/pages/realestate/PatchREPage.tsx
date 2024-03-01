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

    }
  };

  const handleFormSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      const response = await axios.patch<types.RealEstate>(`/api/realestate/${id}`, {
        title,
        description,
        price,
      });
    } catch (error: any) {
      
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