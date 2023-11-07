import {Routes, Route} from 'react-router-dom'
import {MainLayout, RootLayout} from "./components/layout";
import { PostREPage, MapPage } from './pages/realestate';
import {RoutePath} from "./common/configData";
import NotFound from './pages/NotFound';
import { CallbackPage, DormantPage, LoginPage, SignupPage } from './pages/auth';

export default function Routing() {
    return (
        <Routes>
            
            <Route path={RoutePath.root} element={<MainLayout/>}>
                <Route path={RoutePath.root} element={<RootLayout/>}/>
                <Route path={RoutePath.map} element={<MapPage/>}/>
                <Route path={RoutePath.postRE} element={<PostREPage/>}/>

                <Route path="*" element={<NotFound/>}/>
            </Route>
            <Route path={RoutePath.signup} element={<SignupPage/>}></Route>
            <Route path={RoutePath.dormant} element={<DormantPage/>}></Route>
            <Route path={RoutePath.callback} element={<CallbackPage/>}></Route>
            <Route path={RoutePath.login} element={<LoginPage/>}></Route>
        </Routes>
    )
}