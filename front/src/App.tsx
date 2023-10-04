import { BrowserRouter } from 'react-router-dom';
import { RecoilRoot } from 'recoil';
import {SocketContext as Sc, socket as s} from './common/states/contextSocket';
import 'react-toastify/dist/ReactToastify.css';
import { theme } from './common/styles/Theme.style';
import { ThemeProvider } from '@mui/material';
import Cursor from './components/util/Cursor';
import Routing from './Routing';
import { SocketProvider } from './common/states/socketContext';
import { AuthProvider } from './common/states/AuthContext';

function App() {

  return (
    <div onContextMenu={(e) => e.preventDefault()}>
      <BrowserRouter>
      <ThemeProvider theme={theme}>
        <RecoilRoot>
          <SocketProvider>
          <Sc.Provider value={s}>
            <AuthProvider>
            <Routing />
            </AuthProvider>
          </Sc.Provider>
          </SocketProvider>
        </RecoilRoot>
      </ThemeProvider>
      </BrowserRouter>
    </div>
  );
}
export default App;
