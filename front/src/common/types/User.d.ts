export interface User {
    email: string;
    username: string;
    ///////////////////////////////////
    id: number;
    avatar? : string;
    userName: string;       // 유저 이름
    userStatus: string;
    block?: boolean;        // 차단했는지
    relate?: string; //api에서 받아온 차단 및 친구 상태. accepted, blocked, pending.
}

export interface RealEstate {
    id: number;
    title: string;
    description: string;
    price: number;
    image: string;
    image2: string;
    image3: string;
    image4: string;
    image5: string;
    image6: string;
    image7: string;
    image8: string;
    image9: string;
    image10: string;
    soldout: boolean;
    relay_object_type : string; //중계대상물종류
    location : string; //소재지
    area: number; //면적(제곱미터)
    transaction_type : string; //거래형태
    residence_availability_date : string; //입주가능일
    administrative_agency_approval_date : string; //행정기관승인날짜
    number_of_cars_parked: number; //주차대수
    direction : string; //방향
    administration_cost : number; //관리비
    administration_cost2 : number; //사용료
    latitude : number; //위도
    longitude : number; //경도
  }