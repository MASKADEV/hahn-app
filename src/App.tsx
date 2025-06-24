import { Routes, Route } from "react-router-dom";
import { Login } from "./views/authentication/Login.tsx";
import { Register } from "./views/authentication/Register.tsx";
import { NotFound } from "./views/NotFound.tsx";
import ProtectedRoute from "./component/ProtectedRoute.tsx";
import PublicRoute from "./component/PublicRoute.tsx";
import ProductForm from "./views/product/ProductForm.tsx";
import Home from "./views/Home.tsx";

const App = () => {
    return (
        <Routes>
            <Route element={<ProtectedRoute />}>
                <Route path="/" element={<Home />} />
                <Route path="/products/new" element={<ProductForm />} />
                <Route path="/products/edit/:id" element={<ProductForm />} />
            </Route>

            <Route element={<PublicRoute />}>
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
            </Route>

            <Route path="/*" element={<NotFound />} />
        </Routes>
    );
}

export default App;