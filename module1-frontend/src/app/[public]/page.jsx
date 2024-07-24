// import Navbar from "@/components/Navbar";
// import WelcomingText from "@/components/WelcomingText";
// import LiveCaroussel from '@/components/LiveCaroussel';


// export default function Home() { 
//   return (
//     <>  
//       <Navbar />
//       <WelcomingText />
//       <LiveCaroussel />
//     </>
//   );
// }
"use client"
import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import jwtDecode from 'jwt-decode';
import Navbar from "@/components/Navbar";
import WelcomingText from "@/components/WelcomingText";
import LiveCaroussel from '@/components/LiveCaroussel';
import { RingLoader } from 'react-spinners'; // Import the desired spinner

export default function Home() {
  const [user, setUser] = useState(null);
  const router = useRouter();

  useEffect(() => {
    const token = localStorage.getItem('access-token');

    if (!token) {
      router.push('/auth/jeunes');
      return;
    }

    try {
      const decodedToken = jwtDecode(token);
      setUser(decodedToken);
    } catch (error) {
      console.error('Invalid token:', error);
      router.push('/auth/jeunes');
    }
  }, [router]);

  if (!user) {
    return (
      <div className="flex items-center justify-center h-screen">
        <RingLoader color="#3498db" />
      </div>
    );
  }

  return (
    <>
      <Navbar user={user} />
      <WelcomingText user={user} />
      <LiveCaroussel />
    </>
  );
}
