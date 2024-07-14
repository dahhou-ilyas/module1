import Navbar from "@/components/Navbar";
import WelcomingText from "@/components/WelcomingText";
import LiveCaroussel from "@/components/LiveCaroussel";

export default function Home() {
  return (
    <>  
      <Navbar />
      <WelcomingText />
      <LiveCaroussel />
    </>
  );
}
