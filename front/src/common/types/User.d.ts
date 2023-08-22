export interface Record {
    idx: number;
    p1: string;
    p2: string;
    p1Score: number;
    p2Score: number;
}

export interface User {
    id: number;
    avatar? : string;
    userName: string;       // 유저 이름
    myProfile: boolean;     // 내 프로필인지
    // userStatus: boolean;    // 접속 상태
    userStatus: string;
    rank: number;           // 랭크
    odds: number;           // 승률
    record: Record[];       // 전적
    // 본인 정보가 아닐 경우에 추가되는 정보
    following?: boolean;    // 팔로우 중인지
    block?: boolean;        // 차단했는지
    relate?: string; //api에서 받아온 차단 및 친구 상태. accepted, blocked, pending.
}

export interface Friend {
    userId: number;
    me?: User;
    you?: User;
    relate?: string; //차단, 친구추가 수락대기, 친구.
    userName: string;
    // userStatus: boolean;
    userStatus: string;
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