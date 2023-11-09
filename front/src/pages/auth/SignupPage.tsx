import { Button, TextField } from "@mui/material";
import { useEffect, useState } from "react";
import { DefaultButton } from "../../components/common";
import axios from "axios";
import { toast } from "react-toastify";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../common/states/AuthContext";
import React from 'react';
import { Grid } from '@mui/material';
import EmailIcon from '@mui/icons-material/Email';


export default function SignupPage() {
  const [email, setEmail] = useState<string>('');
  const [emailCode, setEmailCode] = useState<string>('');
  const [pwd, setPwd] = useState<string>('');
  const [pwd2, setPwd2] = useState<string>('');
  const [name, setName] = useState<string>('');
  const [pwdHelperText, setPwdHelperText] = useState<string>('');
  const [emailHelperText, setEmailHelperText] = useState<string>('');
  const navigate = useNavigate();
  const Auth = useAuth();
  const [finish, setFinish] = useState<boolean>(false);
  const [waitCode, setWaitCode] = useState<boolean>(false);
  const [sendBtnClicked, setSendBtnClicked] = useState<boolean>(false);
  const [samePwd, setSamePwd] = useState<boolean>(false);
  const [statusEmail, setStatusEmail] = useState<boolean>(false);
  const specialCharsPattern = /[@#$%^&*()_+{}\[\]:;<>,.?~\\\/-]/;
  const specialCharsPattern2 = /[{}\[\]:;<>,.?~\\\/]/;
  const emailPattern = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;

  useEffect(() => {
    if (pwd === ''){
      setSamePwd(false);
      setPwdHelperText('빈 문자열');
    } else if (pwd.length < 8){
      setSamePwd(false);
      setPwdHelperText('8자리 이상 가능');
    } else if (specialCharsPattern2.test(pwd)){
      setSamePwd(false);
      setPwdHelperText('@#$%^&*()_+ 특수문자만 사용가능');
    } else if (pwd === pwd2){
      setSamePwd(true);
      setPwdHelperText('비밀번호 일치');
    } else {
      setSamePwd(false);
      setPwdHelperText('비밀번호 불일치');
    }
  }, [pwd,pwd2]);

  useEffect(() => {
    if (email === ''){
      setStatusEmail(false);
      setEmailHelperText('빈 문자열');
    } else if (emailPattern.test(email)){
      setStatusEmail(true);
      setEmailHelperText('');
    } else {
      setStatusEmail(false);
      setEmailHelperText('형식을 확인해주세요');
    }
  }, [email]);

//   useEffect(() => {
//     if (Auth.isLoggedIn) closeWindow();
//  }, [Auth]);

//  useEffect(() => {
//   if (!finish) return;
//   function checkWindowStatus() {
//     if (finish) {
//       closeWindow();
//     }
// }
// setInterval(checkWindowStatus, 1000);
// }, [finish]);

  const handleSignupSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!samePwd) {
      alert('비밀번호를 확인해주세요');
      toast.info('비밀번호를 확인해주세요');
      return;
    }
    if (!statusEmail) {
      alert('이메일을 확인해주세요');
      toast.info('이메일을 확인해주세요');
      return;
    }
    try {
        const response = await axios.post('/api/user/signup', {
            email : email,
            emailcode : emailCode,
            password : pwd,
            username : name,
        }, { 
          withCredentials: true, 
        });
        alert('회원가입 성공');
        closeWindow();
    } catch (error: any) {
        toast.info("이메일/인증코드를 다시 확인해주세요");
    }
  };

  const handleEmailConfirm = async (e: React.FormEvent) => {
    setSendBtnClicked(true);
    e.preventDefault();
    try {
        const response = await axios.post('/api/auth/email/code', {
            email : email,
        }, { 
          withCredentials: true, 
        });
        setWaitCode(true);
    } catch (error: any) {
      setSendBtnClicked(false);
      alert("이미 가입중이거나 입력한 메일을 다시 확인해주세요");
      toast.info("이미 가입중이거나 입력한 메일을 다시 확인해주세요");
    }
  };

  const closeWindow = () => {
    window.close();
  }

  return (
    <center>
      <h1>회원가입</h1>
      <hr/>
      <Grid container spacing={2}>
      <Grid item xs={12}>
        <div style={{   }}>
          <TextField
            type="email"
            style={{ width:'70vh' }}
            className='textField'
            label="이메일 / EMAIL"
            variant="outlined"
            size="small"
            value={email}
            onChange={e => setEmail(e.target.value)}
            helperText={emailHelperText}
            error={!statusEmail}
            disabled={waitCode}
            autoComplete="off"
          />
        </div>
      </Grid>
      <Grid item xs={12}>
        <div style={{   }}>
          {waitCode ?
            <TextField
            style={{ width:'70vh' }}
            className='textField'
            label="인증코드 / EMAILCODE"
            variant="outlined"
            size="small"
            value={emailCode}
            onChange={e => setEmailCode(e.target.value)}
            helperText={"5분안에 입력해주세요"}
            autoComplete="off"
            />
            :
            sendBtnClicked ? <>인증코드 전송중...</> :
            <Button variant="contained" color="primary" onClick={handleEmailConfirm}>
            이메일 인증하기
            </Button>
          }
        </div>
      </Grid>
      <Grid item xs={12}>
        <div style={{   }}>
        <TextField
            style={{ width:'70vh'  }}
            className='textField'
            label="성함 / NAME"
            variant="outlined"
            size="small"
            value={name}
            onChange={e => setName(e.target.value)}
            autoComplete="off"
          />
        </div>
      </Grid>
      <Grid item xs={12}>
        <div style={{   }}>
        <TextField
            type="password"
            style={{ width:'70vh'  }}
            className='textField'
            label="비밀번호 / PASSWORD"
            variant="outlined"
            size="small"
            value={pwd}
            onChange={e => setPwd(e.target.value)}
            error={!samePwd}
            autoComplete="off"
          />
        </div>
      </Grid>
      <Grid item xs={12}>
        <div style={{   }}>
        <TextField
            type="password"
            style={{ width:'70vh'  }}
            className='textField'
            label="비밀번호 확인 / PASSWORD"
            variant="outlined"
            size="small"
            value={pwd2}
            onChange={e => setPwd2(e.target.value)}
            helperText={pwdHelperText}
            error={!samePwd}
            autoComplete="off"
          />
        </div>
      </Grid>
      <Grid item xs={12}>
        <div style={{   }}>
          <Button variant="contained" color="primary" onClick={handleSignupSubmit}>
              가입하기
          </Button>
        </div>
      </Grid>
      
    </Grid>




      {/* <TextField
        className='textField'
        label="이메일 / EMAIL"
        variant="outlined"
        size="small"
        value={email}
        onChange={e => setEmail(e.target.value)}
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
      </Button> */}

    </center>
  );
}
