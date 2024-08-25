// "use client"
// import { useEffect, useState } from 'react';
// import { useRouter } from 'next/navigation';
// import {jwtDecode} from 'jwt-decode';
// import Navbar from "@/components/Navbar";
// import WelcomingText from "@/components/WelcomingText";
// import LiveCaroussel from '@/components/LiveCaroussel';
// import { PacmanLoader
//  } from 'react-spinners'; // Import the desired spinner

// export default function Home() {
//   const [user, setUser] = useState(null);
//   const router = useRouter();
//   const [loading, setLoading] = useState(true);

//   useEffect(() => {
//     const token = localStorage.getItem('access-token');

//     if (!token) {
//       router.push('/auth/jeunes');
//       return;
//     }

//     try {
//       const decodedToken = jwtDecode(token);
//       console.log(decodedToken);
//       setUser(decodedToken);
//     } catch (error) {
//       console.error('Invalid token:', error);
//       router.push('/auth/jeunes');
//       return;
//     }finally {
//       setLoading(false);
//     }
//   }, [router]);

//   if (loading) {
//     return (
//       <div className="flex items-center justify-center h-screen">
//         <PacmanLoader
//  color="#1e234a" />
//       </div>
//     );
//   }

//   if (!user) {
//     return (
//       <div className="flex items-center justify-center h-screen">
//         <RingLoader color="#3498db" />
//       </div>
//     );
//   }

//   return (
//     <>
//       <Navbar user={user} />
//       <WelcomingText user={user} />
//       <LiveCaroussel />
//     </>
//   );
// }
"use client"
import { useEffect, useState } from "react";
import "@/assets/css/style.css";
import "@/assets/css/bootstrap.min.css";
import Navbar from "@/components/Header";
import Sidebar from "@/components/Sidebar";
import WelcomingText from "@/components/WelcomingText";

import CardsCaroussel from "@/components/CardsCaroussel";
import { useTranslations, useLocale } from 'next-intl';

export default function Home() { 
  const [isSmallScreen, setIsSmallScreen] = useState(false);
  const t = useTranslations('Navbar');
  const locale = useLocale();

useEffect(() => {
  const handleResize = () => {
    setIsSmallScreen(window.innerWidth <= 768);
  };

  handleResize();
  window.addEventListener("resize", handleResize);

  return () => window.removeEventListener("resize", handleResize);
}, []);

  return (
    
    <>   
    <Navbar t={t} locale={locale}/>
    <div id="root">
      
      <Sidebar t={t}/>
      <div className="page-wrapper">
        <div className="content">
         
          
      <WelcomingText />
      {/* <LiveCaroussel /> */}
      <CardsCaroussel />
      </div>
      </div>
      </div>
    </>
  );
}