import React, {useRef, useState } from 'react';
import axios from 'axios';
import * as types from "../../common/types/User";
import { toast } from "react-toastify";
import { REACT_APP_HOST } from "../../common/configData";
import { Avatar } from '@mui/material';
import { ScrollableWrapper } from '../../components/realestate/ScrollableWrapper.style'

function PostREPage() {
  const [title, setTitle] = useState<string>('');
  const [description, setDescription] = useState<string>('');
  const [price, setPrice] = useState<number>(0);
  const [imageFile, setImageFile] = useState('');
  const [soldout, setSoldout] = useState<boolean>(false);
  const inputRef = useRef<HTMLInputElement | null> (null);
  const [uploadedId, setUploadedId] = useState<string>('');
  const [relay_object_type, setRelay_object_type] = useState<string>('');
  const [location, setLocation] = useState<string>('');
  const [area, setArea] = useState<number>(0);
  const [transaction_type, setTransaction_type] = useState<string>('');
  const [residence_availability_date, setResidence_availability_date] = useState<string>('');
  const [administrative_agency_approval_date, setAdministrative_agency_approval_date] = useState<string>('');
  const [number_of_cars_parked, setNumber_of_cars_parked] = useState<number>(0);
  const [direction, setDirection] = useState<string>('');
  const [administration_cost, setAdministration_cost] = useState<number>(0);

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
            relay_object_type : relay_object_type, //중계대상물종류
            location : location, //소재지 (지번, 동, 호수)
            area: area, //면적(제곱미터)
            transaction_type : transaction_type, //거래형태
            residence_availability_date : residence_availability_date, //입주가능일
            administrative_agency_approval_date : administrative_agency_approval_date, //행정기관승인날짜
            number_of_cars_parked: number_of_cars_parked, //주차대수
            direction : direction, //방향
            administration_cost : administration_cost, //관리비
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
    
    <ScrollableWrapper>
    <div>
    <h2>부동산 매물 등록</h2>
      <form onSubmit={handleFormSubmit}>
        <div>
          <label htmlFor="title"><h3>제목</h3></label>
          <textarea
            id="title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            required
          ></textarea>
          <hr/>
        </div>
        <div>
          <label htmlFor="price"><h3>가격</h3></label>
          <input
            type="number"
            id="price"
            value={price}
            onChange={(e) => setPrice(Number(e.target.value))}
            required
          />
          <hr/>
        </div>
        <Avatar src={imageFile} alt="real-estate image" variant="rounded" sx={{ width: 300, height: 250 }} />
        <div>
          <label htmlFor="image"><h3>사진</h3></label>
          <input
            type="file"
            id="image"
            accept="image/*"
            onChange={handleImageChange}
            ref={inputRef}
          />
          <hr/>
        </div>
        <div>
          <label htmlFor="area"><h3>면적</h3></label>
          <input
            type="number"
            id="area"
            value={area}
            onChange={(e) => setArea(Number(e.target.value))}
            required
          />
          <br/>단위 : 제곱미터
          <hr/>
        </div>
        <div>
          <label htmlFor="number_of_cars_parked"><h3>주차대수</h3></label>
          <input
            type="number"
            id="number_of_cars_parked"
            value={number_of_cars_parked}
            onChange={(e) => setNumber_of_cars_parked(Number(e.target.value))}
            required
          />
          <br/>총가능한 주차대수 또는 세대당 가능한 주차대수
          <hr/>
        </div>
        <div>
          <label htmlFor="relay_object_type"><h3>중계대상물종류</h3></label>
          <textarea
            id="relay_object_type"
            value={relay_object_type}
            onChange={(e) => setRelay_object_type(e.target.value)}
            required
          ></textarea>
          <br/>단독주택,공동주택,제1종근린생활등
          <hr/>
        </div>
        <div>
          <label htmlFor="location"><h3>소재지</h3></label>
          <textarea
            id="location"
            value={location}
            onChange={(e) => setLocation(e.target.value)}
            required
          ></textarea>
          <br/>-(단독주택) 지번포함 (단,중개의뢰인 요청시, 읍면동리까지 표시가능)
          <br/>-(그외주택) 지번,동,층수포함
          <br/>(단,중개의뢰인요청시,저/중/고로 표시가능)
          <br/>-(주택을 제외한 건축물) 
          <br/>읍면동리까지 표시가능, 층수포함
          <hr/>
        </div>
        <div>
          <label htmlFor="transaction_type"><h3>거래형태</h3></label>
          <textarea
            id="transaction_type"
            value={transaction_type}
            onChange={(e) => setTransaction_type(e.target.value)}
            required
          ></textarea>
          <br/>-매매/교환/임대차/그 밖의 권리득실변경
          <hr/>
        </div>
        <div>
          <label htmlFor="residence_availability_date"><h3>입주가능일</h3></label>
          <input
            type='date'
            id="residence_availability_date"
            value={residence_availability_date}
            onChange={(e) => setResidence_availability_date(e.target.value)}
            required
          />
          <br/>-'즉시입주' 혹은 입주 가능한 세부날짜를 
          <br/>표시해야 함
          <hr/>
        </div>
        <div>
          <label htmlFor="administrative_agency_approval_date"><h3>행정기관승인일자</h3></label>
          <input
            type='date'
            id="administrative_agency_approval_date"
            value={administrative_agency_approval_date}
            onChange={(e) => setAdministrative_agency_approval_date(e.target.value)}
            required
          />
          <br/>-사용검사일/사용승인일/준공인가일 중 
          <br/>선택하여세부날짜를 표시해야 함
          <hr/>
        </div>
        <div>
          <label htmlFor="direction"><h3>방향</h3></label>
          <textarea
            id="direction"
            value={direction}
            onChange={(e) => setDirection(e.target.value)}
            required
          ></textarea>
          <br/>-방향기준과 함께 표시해야 함
          <br/>(거실이나 안방등 주식의 방향기준)
          <hr/>
        </div>
        <div>
          <label htmlFor="administration_cost"><h3>관리비</h3></label>
          <input
            type="number"
            id="administration_cost"
            value={administration_cost}
            onChange={(e) => setAdministration_cost(Number(e.target.value))}
            required
          />
          <br/>관리비와 사용료를 명확히 구분하여 표시해야 함
          <hr/>
        </div>
        <div>
          <label htmlFor="description"><h3>기타 세부 사항</h3></label>
          <textarea
            id="description"
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            required
          ></textarea>
          <br/>예) 가스레인지, 냉장고, 세탁기, 옷장, 책상, 침대, 벽걸이에어컨 있습니다.
          <br/> 현관보안 비밀번호 구비되어있습니다.
          <hr/>
        </div>
        <div>
          <label htmlFor="soldout"><h3>거래완료 여부</h3></label>
          <label>
              <input type="radio" name="soldout" id="soldout" defaultChecked={!soldout} value="true" onChange={(e) => setSoldout(false)} /> 거래 중
          </label>
          <label>
              <input type="radio" name="soldout" id="soldout" defaultChecked={soldout} value="false" onChange={(e) => setSoldout(true)} /> 거래 완료
          </label>
          <hr/>
        </div>
        <button type="submit"><h3>매물 등록</h3></button>
      </form>
    </div>
    </ScrollableWrapper>
  );
}

export default PostREPage;