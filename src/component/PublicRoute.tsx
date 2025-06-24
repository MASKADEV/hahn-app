import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { ProgressSpinner } from 'primereact/progressspinner';

const PublicRoute= () => {
    const { user, isLoading } = useAuth();
    const location = useLocation();

    if (isLoading) {
        return (
            <div className="flex align-items-center justify-content-center min-h-screen">
                <ProgressSpinner />
            </div>
        );
    }

    if (user) {
        return <Navigate to={location.state?.from || '/'} replace />;
    }

    return <Outlet />;
}

export default PublicRoute;