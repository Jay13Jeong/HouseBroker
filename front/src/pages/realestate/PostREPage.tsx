import React, { useState } from 'react';
import axios from 'axios';
import * as types from "../../common/types/User";
import { toast } from "react-toastify";
import { REACT_APP_HOST } from "../../common/configData";

function PostREPage() {
  const [title, setTitle] = useState<string>('');
  const [description, setDescription] = useState<string>('');
  const [price, setPrice] = useState<number>(0);
  const [imageFile, setImageFile] = useState<File | null>(null);

  const handleFormSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
    const response = await axios.post('/api/realestate/', {
        title: title,
        description: description,
        price: price,
        image: imageFile,
    }, { withCredentials: true });

      toast.success("올리기 성공");
      // 성공 메시지 또는 리다이렉트 등의 추가 작업 수행
    } catch (error: any) {
      toast.error(error.response.data.message);
      // 실패 메시지 또는 오류 처리 등의 추가 작업 수행
    }
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      const selectedFile = e.target.files[0];
      setImageFile(selectedFile);
    }
  };

  return (
    <div>
      <h2>부동산 매물 등록</h2>
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
        <div>
          <label htmlFor="image">사진</label>
          <input
            type="file"
            id="image"
            accept="image/*"
            onChange={handleImageChange}
          />
        </div>
        <button type="submit">등록</button>
      </form>
    </div>
  );
}

export default PostREPage;