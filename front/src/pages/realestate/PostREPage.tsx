import React, {useRef, useState } from 'react';
import axios from 'axios';
import * as types from "../../common/types/User";
import { toast } from "react-toastify";
import { REACT_APP_HOST } from "../../common/configData";
import { Avatar } from '@mui/material';

function PostREPage() {
  const [title, setTitle] = useState<string>('');
  const [description, setDescription] = useState<string>('');
  const [price, setPrice] = useState<number>(0);
  const [imageFile, setImageFile] = useState('');
  const [soldout, setSoldout] = useState<boolean>(false);
  const inputRef = useRef<HTMLInputElement | null> (null);
  const [uploadedId, setUploadedId] = useState<string>('');

  const handleImageSubmit = async(e: React.FormEvent) => {
    e.preventDefault();
    if (!(inputRef.current && inputRef.current.value))
        return;
    try {
        const response = await axios.post('/api/realestate/image', {
            id: uploadedId,
            image: inputRef.current.files![0]
        }, { withCredentials: true, headers: { 'Content-Type': 'multipart/form-data' } });
        toast.success("이미지 올리기 성공");
    } catch (error: any) {
        toast.error("이미지 올리기 실패");
        toast.error(error.response.data.message);
    }
  };

//   const handleFormSubmit = async (e: React.FormEvent) => {
//     e.preventDefault();

//     try {
//         const response = await axios.post('/api/realestate/', {
//             title: title,
//             description: description,
//             price: price,
//             image: null,
//         }, { withCredentials: true });
//         toast.success("올리기 성공");
//         setUploadedId(response.data.message);
//         console.log("uplooo")
//         console.log(response);
//         handleImageSubmit(e);
//     } catch (error: any) {
//         toast.error("올리기 실패");
//         toast.error(error.response.data.message);
//     }
//   };

const handleFormSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    let imageData = null;
    if (inputRef.current && inputRef.current.value)
        imageData = inputRef.current.files![0]
    try {
        const response = await axios.post('/api/realestate/', {
            title: title,
            description: description,
            price: price,
            image: imageData,
            soldout : soldout,
        }, { withCredentials: true, headers: { 'Content-Type': 'multipart/form-data' } });
        toast.success("매물 올리기 성공");
    } catch (error: any) {
        toast.error("매물 올리기 실패");
        toast.error(error.response.data.message);
    }
  };

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files.length > 0) {
      const selectedFile = e.target.files[0];
      if (selectedFile.size >= ((1 << 20) * 4))
          throw("4MB미만 업로드 가능.");
      setImageFile(URL.createObjectURL(selectedFile));
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
        <Avatar src={imageFile} alt="real-estate image" variant="rounded" sx={{ width: 300, height: 250 }} />
        <div>
          <label htmlFor="image">사진</label>
          <input
            type="file"
            id="image"
            accept="image/*"
            onChange={handleImageChange}
            ref={inputRef}
          />
        </div>
        <div>
          <label htmlFor="price">거래완료 여부</label>
          <label>
              <input type="radio" name="is_sell" id="is_sell" value={"거래 중"} onChange={(e) => setSoldout(false)} /> 거래 중
          </label>
          <label>
              <input type="radio" name="is_sell" id="is_sell" value={"거래 완료"} onChange={(e) => setSoldout(true)} /> 거래 완료
          </label>
        </div>
        <hr></hr>
        <button type="submit">매물 등록</button>
      </form>
    </div>
  );
}

export default PostREPage;