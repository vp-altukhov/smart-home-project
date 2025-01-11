import React from 'react';
import './App.css';
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import LoginForm from "./LoginForm";
import Home from "./Home";
import DeviceTabs from "./DeviceTabs";
import {useAuth} from "./hooks/useAuth";

function App() {

    const auth = useAuth();

    const router = createBrowserRouter([
        {
            path: "/",
            element: <Home />
        },
        {
            path: "/login",
            element: <LoginForm />
        },
        {
            path: "/device/:id",
            element: <DeviceTabs />
        }
    ], { basename: "/" })

    return (
        <RouterProvider router={router} />
    )
}

export default App;
