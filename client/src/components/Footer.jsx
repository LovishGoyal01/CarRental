import React from 'react'
import {assets} from '../assets/assets'
import {motion} from "motion/react"
import { Link } from "react-router-dom";


const Footer = () => {
  return (
      <motion.div initial={{y:30, opacity:0}} whileInView={{y:0, opacity:1}} transition={{duration: 0.6}} className="px-6 md:px-16 lg:px-24 xl:px-32 mt-60 text-sm text-gray-500">
            <motion.div initial={{y:20, opacity:0}} whileInView={{y:0, opacity:1}} transition={{duration: 0.6,delay:0.2}} className='flex flex-wrap justify-between items-start gap-8 pb-6 border-borderColor border-b'>
                <div>
                    <motion.img initial={{opacity:0}} whileInView={{opacity:1}} transition={{duration: 0.5,delay:0.3}} src={assets.logo} alt="logo" className=' h-8 md:h-9' />
                    <motion.p initial={{opacity:0}} whileInView={{opacity:1}} transition={{duration: 0.5,delay:0.4}} className='max-w-80 mt-3'>
                        Premium car rental service with a wide selection of luxury and everyday vehicles for all your driving needs.
                    </motion.p>
                    <motion.div initial={{opacity:0}} whileInView={{opacity:1}} transition={{duration: 0.5,delay:0.5}} className='flex items-center gap-3 mt-6'>
                       <a href="https://www.facebook.com/lovish.goyal.9619/" target="_blank" rel="noopener noreferrer"><img src={assets.facebook_logo} className="w-5 h-5" alt="" /></a>
                       <a href="https://www.instagram.com/lovishgoyal15/" target="_blank" rel="noopener noreferrer"><img src={assets.instagram_logo} className="w-5 h-5" alt="" /></a>
                       <a href="https://x.com/LovishGoyal2005" target="_blank" rel="noopener noreferrer"><img src={assets.twitter_logo} className="w-5 h-5" alt="" /></a>
                       <a href="mailto:goyallovish852@gmail.com"><img src={assets.gmail_logo} className="w-5 h-5" alt="" /></a>
                    </motion.div>
                </div>

                <motion.div initial={{y:20, opacity:0}} whileInView={{y:0, opacity:1}} transition={{duration: 0.6,delay:0.4}} className='flex flex-wrap justify-between w-1/2 gap-8'>
                    <div>
                       <h2 className='text-base font-medium text-gray-800 uppercase'>Quick Links</h2>
                       <ul className='mt-3 flex flex-col gap-1.5 '>
                           <li><Link to="/">Home</Link></li>
                           <li><Link to="/cars">Browse Cars</Link></li>
                           <li><Link to="/owner">List Your Car</Link></li>
                           <li><Link to="/my-bookings">Your Bookings</Link></li>
                       </ul>
                    </div>
                    <div>
                       <h2 className='text-base font-medium text-gray-800 uppercase'>Resources</h2>
                       <ul className='mt-3 flex flex-col gap-1.5 '>
                           <li><a href="#">Help Center</a></li>
                           <li><a href="#">Terms of Service</a></li>
                           <li><a href="#">Privacy Policy</a></li>
                           <li><a href="#">Insurance</a></li>
                       </ul>
                    </div>
                    <div>
                       <h2 className='text-base font-medium text-gray-800 uppercase'>Contact</h2>
                       <ul className='mt-3 flex flex-col gap-1.5 '>
                           <li>Harpal Nagar, Street No. 7</li>
                           <li>Bathinda, Punjab, India • 151001</li>
                           <li><a href="tel:+917009216273">+91 7009216273</a></li>
                           <li><a href="mailto:goyallovish852@gmail.com">goyallovish852@gmail.com</a></li>
                       </ul>
                    </div> 
                </motion.div>    
            </motion.div>

            <motion.div initial={{y:0, opacity:0}} whileInView={{y:0, opacity:1}} transition={{duration: 0.6,delay:0.6}} className='flex flex-col md:flex-row gap-2 items-center justify-between py-5'>
                <p>© {new Date().getFullYear()} CarRental. All rights reserved.</p>
                <ul className='flex items-center gap-4'>
                    <li><a href="#">Privacy</a></li>
                    <li>|</li>
                    <li><a href="#">Terms</a></li>
                    <li>|</li>
                    <li><a href="#">Cookies</a></li>
                </ul>
            </motion.div>
        </motion.div>

  )
}

export default Footer