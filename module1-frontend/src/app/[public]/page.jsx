import {useTranslations} from 'next-intl';
import Navbar from "@/components/Navbar";
import WelcomingText from "@/components/WelcomingText";
import LiveCaroussel from '@/components/LiveCaroussel';


export default function Home() { 
    const t = useTranslations('HomePage');
  return (
    <>  
      <Navbar />
      <WelcomingText />
      <LiveCaroussel />
      <h1>{t('title')}</h1>
    </>
  );
}
