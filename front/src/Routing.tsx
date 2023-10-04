import {Routes, Route} from 'react-router-dom'
import {MainLayout, RootLayout} from "./components/layout";
import { PostREPage, MapPage } from './pages/realestate';
import {RoutePath} from "./common/configData";
import NotFound from './pages/NotFound';

export default function Routing() {
    return (
        <Routes>
            
            <Route path={RoutePath.root} element={<MainLayout/>}>
                <Route path={RoutePath.root} element={<RootLayout/>}/>
                <Route path={RoutePath.map} element={<MapPage/>}/>
                <Route path={RoutePath.postRE} element={<PostREPage/>}/>

                <Route path="*" element={<NotFound/>}/>
            </Route>
        </Routes>
    )
}