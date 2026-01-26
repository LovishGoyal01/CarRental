import React, { useEffect } from 'react'
import axios from 'axios';
import { useDispatch, useSelector } from 'react-redux';
import { Route, Routes, useLocation, useNavigate } from 'react-router-dom';

import Navbar from './components/Navbar'
import Home from './pages/Home';
import CarDetails from './pages/CarDetails';
import Cars from './pages/Cars';
import MyBookings from './pages/MyBookings';
import Footer from './components/Footer';
import Layout from './pages/owner/Layout';
import Dashboard from './pages/owner/Dashboard';
import ManageCars from './pages/owner/ManageCars';
import AddCar from './pages/owner/AddCar';
import ManageBookings from './pages/owner/ManageBookings';
import Login from './components/Login';

import { addUser } from './store/userSlice';
import { setCars } from './store/carsSlice';
import { setIsOwner, setToken } from './store/appSlice';

import toast, { Toaster } from 'react-hot-toast'

const App = () => {

  const dispatch = useDispatch();
  const navigate = useNavigate();

  const Base_URL = import.meta.env.VITE_BASE_URL

  const {showLogin, token} = useSelector((state) => state.app);

  const isOwnerPath = useLocation().pathname.startsWith('/owner');

  const fetchUser = async () => {
     try{
        const {data} = await axios.get(Base_URL + '/api/user/data')      
        if(data.success){
          dispatch(addUser(data.user));
          dispatch(setIsOwner(data.user.role === 'owner'));
        }else{ 
          navigate('/')
        }    
     }catch(error){
        toast.error(error.message)
     }
  }

  const fetchCars = async () => {
     try{
        const {data} = await axios.get(Base_URL + '/api/user/cars')
         data.success ? dispatch(setCars(data.cars)) : toast.error(data.message)
     }catch(error){
        toast.error(error.message)
     }
  }
  
  //useEffect to retrieve the token from local storage
  useEffect(()=>{
    const token = localStorage.getItem('token')
    dispatch(setToken(token)); 
    fetchCars()
  },[])

  //useEffect to fetch user data when token is available
  useEffect(()=>{
    if(token){
      axios.defaults.headers.common['Authorization'] = `${token}`
      fetchUser();
    }
  },[token])

  return (
    <>
      <Toaster />
      
      {showLogin && <Login />}

      {!isOwnerPath && <Navbar /> }

      <Routes>
         <Route path='/' element={<Home/>} />
         <Route path='/car-details/:id' element={<CarDetails/>} />
         <Route path='/cars' element={<Cars/>} />
         <Route path='/my-bookings' element={<MyBookings/>} />
         <Route path='/owner' element={<Layout />}>
             <Route index element={<Dashboard />} />
             <Route path='add-car' element={<AddCar />} />
             <Route path='manage-cars' element={<ManageCars />} />
             <Route path='manage-bookings' element={<ManageBookings />} />
         </Route>
      </Routes>

      {!isOwnerPath && <Footer /> }
      
    </>
  )
}

export default App