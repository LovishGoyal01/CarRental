import { configureStore } from "@reduxjs/toolkit";
import UserReducer from "./userSlice";
import CarsReducer from "./carsSlice";
import AppReducer from "./appSlice";

const appStore = configureStore({
    reducer:{
        user:UserReducer,
        cars:CarsReducer,
        app:AppReducer,
    }
});

export default appStore;