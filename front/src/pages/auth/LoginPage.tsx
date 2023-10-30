import { Button, TextField } from "@mui/material";
import { useEffect, useState } from "react";
import { DefaultButton } from "../../components/common";
import axios from "axios";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../common/states/AuthContext";

export default function LoginPage() {
  const [email, setEmail] = useState<string>('');
  const [pwd, setPwd] = useState<string>('');
  const navigate = useNavigate();
  const Auth = useAuth();
  const [finish, setFinish] = useState<boolean>(false);

  useEffect(() => {
    if (Auth.isLoggedIn) closeWindow();
 }, [Auth]);

 useEffect(() => {
  if (!finish) return;
  function checkWindowStatus() {
    if (finish) {
      closeWindow();
    }
}
setInterval(checkWindowStatus, 1000);
}, [finish]);

  const handleLoginSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
        const response = await axios.post('/api/user/', {
            email : [email],
            password : [pwd],
            username : '',
        }, { 
          withCredentials: true, 
          // headers: { 'Content-Type': 'multipart/form-data' } 
        });
        closeWindow();
        setFinish(true);
        // toast.success("성공");
    } catch (error: any) {
        toast.info("이메일/비밀번호를 다시 확인해주세요.");
        // toast.error(error.response.data.message);
    }
  };

  const closeWindow = () => {
    window.close();
  }

  return (
    <center>

      <TextField
        className='textField'
        label="이메일 / EMAIL"
        variant="outlined"
        size="small"
        value={email}
        onChange={e => setEmail(e.target.value)}
        // onKeyDown={(e) => {
        //     if (e.key === 'Enter'){
        //         handleSendClick(e);  
        //     }
        //   }}
      />
      <br></br>
      <TextField
        className='textField'
        label="비밀번호 / PASSWORD"
        variant="outlined"
        size="small"
        value={pwd}
        onChange={e => setPwd(e.target.value)}
        // onKeyDown={(e) => {
        //     if (e.key === 'Enter'){
        //         handleSendClick(e);  
        //     }
        //   }}
      />
      <br/>
      <Button variant="contained" color="primary" onClick={handleLoginSubmit}>
            로그인
      </Button>
      <Button variant="contained" color="primary" onClick={() => navigate('/auth/signup')}>
            회원가입
      </Button>
      <hr/>
      <Button variant="contained" color="primary" onClick={() => window.location.href = "/api/auth/google/login"}>
      구글 로그인
      </Button>

    </center>
  );
}
