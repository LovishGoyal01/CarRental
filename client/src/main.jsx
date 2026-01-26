
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import { BrowserRouter } from 'react-router-dom'
import { Provider } from "react-redux";
import { MotionConfig } from 'motion/react'
import appStore from './store/appStore.js';

createRoot(document.getElementById('root')).render(
  <BrowserRouter>
    <Provider store={appStore}>
      <MotionConfig viewport={{once:true}} >      
         <App />
      </MotionConfig>
    </Provider>
  </BrowserRouter>,
)
