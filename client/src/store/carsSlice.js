import { createSlice } from "@reduxjs/toolkit";


const carsSlice = createSlice({
    name:"cars",
    initialState:[],
    reducers:{
        setCars:(state,action)=>{
          return action.payload;
        },
    }
});

export const {setCars} = carsSlice.actions;
export default carsSlice.reducer;