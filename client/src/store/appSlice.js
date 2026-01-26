import { createSlice } from "@reduxjs/toolkit";

const initialState = {
  token: localStorage.getItem("token") || null,
  isOwner: false,
  showLogin: false,
  pickupDate:'',
  returnDate:'',
};

const appSlice = createSlice({
  name: "app",
  initialState,
  reducers: {
    setToken: (state, action) => {
      state.token = action.payload;
      if (action.payload) {
        localStorage.setItem("token", action.payload);
      }
    },

    setIsOwner: (state, action) => {
      state.isOwner = action.payload;
    },

    setShowLogin: (state, action) => {
      state.showLogin = action.payload;
    },

    logout: (state) => {
      state.token = null;
      state.isOwner = false;
      state.showLogin = false;
      localStorage.removeItem("token");
    },

    setPickupDate: (state, action)=>{
      state.pickupDate = action.payload;
    },

    setReturnDate: (state, action)=>{
      state.returnDate = action.payload;
    }
  },
});

export const { setToken, setIsOwner, setShowLogin, logout, setPickupDate, setReturnDate } = appSlice.actions;

export default appSlice.reducer;