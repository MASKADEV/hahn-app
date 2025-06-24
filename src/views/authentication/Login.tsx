import { useNavigate } from 'react-router-dom';
import { useAuth } from "../../context/AuthContext.tsx";
import { InputText } from 'primereact/inputtext';
import { Password } from 'primereact/password';
import { Button } from 'primereact/button';
import { Message } from 'primereact/message';
import { classNames } from 'primereact/utils';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm, Controller } from 'react-hook-form';
import * as z from 'zod';
import { Link } from 'react-router-dom';

const loginSchema = z.object({
    username: z.string().min(1, 'Username is required'),
    password: z.string().min(1, 'Password is required')
});

type LoginFormData = z.infer<typeof loginSchema>;

export const Login = () => {
    const { login, isLoading } = useAuth();
    const navigate = useNavigate();

    const {
        handleSubmit,
        control,
        formState: { errors },
        setError,
    } = useForm<LoginFormData>({
        resolver: zodResolver(loginSchema),
        defaultValues: {
            username: '',
            password: ''
        }
    });

    const onSubmit = async (data: LoginFormData) => {
        try {
            await login(data);
            navigate('/', { replace: true });
        } catch (err) {
            setError('root', {
                type: 'manual',
                message: 'Invalid username or password'
            });
            console.error('Login error:', err);
        }
    };

    return (
        <div className="w-screen h-screen flex items-center justify-center">
            <form onSubmit={handleSubmit(onSubmit)} className='flex flex-col gap-3 shadow-1 p-5 rounded w-full max-w-md'>
                {errors.root && (
                    <Message
                        severity="error"
                        text={errors.root.message}
                        className="w-full"
                    />
                )}

                <div className="flex flex-column gap-2">
                    <label htmlFor="username">Username</label>
                    <Controller
                        name="username"
                        control={control}
                        render={({ field, fieldState }) => (
                            <>
                                <InputText
                                    id={field.name}
                                    value={field.value}
                                    onChange={field.onChange}
                                    className={classNames({ 'p-invalid': fieldState.error })}
                                />
                                {fieldState.error && (
                                    <small className="p-error">{fieldState.error.message}</small>
                                )}
                            </>
                        )}
                    />
                </div>

                <div className="flex flex-column gap-2 w-full">
                    <label htmlFor="password">Password</label>
                    <Controller
                        name="password"
                        control={control}
                        render={({ field, fieldState }) => (
                            <>
                                <Password
                                    id={field.name}
                                    value={field.value}
                                    onChange={field.onChange}
                                    toggleMask
                                    feedback={false}
                                    className={classNames('w-full', { 'p-invalid': fieldState.error })}
                                    inputClassName="w-full"
                                />
                                {fieldState.error && (
                                    <small className="p-error">{fieldState.error.message}</small>
                                )}
                            </>
                        )}
                    />
                </div>

                <Button
                    type="submit"
                    label="Login"
                    icon="pi pi-sign-in"
                    loading={isLoading}
                    className="mt-3 w-full"
                />

                <div className="text-center mt-3">
                    <span className="text-sm text-gray-600">New user? </span>
                    <Link
                        to="/register"
                        className="text-sm text-blue-500 hover:text-blue-700 font-medium"
                    >
                        Register here
                    </Link>
                </div>
            </form>
        </div>
    );
};