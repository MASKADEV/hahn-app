import { createBrowserRouter, RouterProvider } from "react-router-dom";
import { Login } from "../views/authentication/Login.tsx";
import { Register } from "../views/authentication/Register.tsx";
import { Home } from "../views/Home.tsx";
import { NotFound } from "../views/NotFound.tsx";
import ProtectedRoute from "../component/ProtectedRoute.tsx";

const router = createBrowserRouter([
    {
        path: "/",
        element: <ProtectedRoute />,
        children: [
            {
                index: true,
                element: <Home />
            }
        ]
    },
    {
        path: "/login",
        element: <Login />
    },
    {
        path: "/register",
        element: <Register />
    },
    {
        path: "/*",
        element: <NotFound />
    }
]);

const Router = () => {
    return <RouterProvider router={router} />;
}

export default Router;