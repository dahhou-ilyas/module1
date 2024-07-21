import React from 'react';
import Image from "next/image";
import { useTranslations } from 'next-intl';
import { LanguageSelector } from '@/components/LanguageSelector';
import Logo from "../../../../public/logoJeune.png";
import { BiArrowBack } from "react-icons/bi";

const Recapitulatif = ({ formData, handleSubmit, prevStep }) => {
  const t = useTranslations('Recapitulatif');

  const formatDate = (dateString) => {
    const options = { year: 'numeric', month: 'long', day: 'numeric' };
    return new Date(dateString).toLocaleDateString('fr-FR', options);
  };

  const data = [
    { label: t('nom'), value: formData.nom },
    { label: t('prenom'), value: formData.prenom },
    { label: t('dateNaissance'), value: formData.dateNaissance ? formatDate(formData.dateNaissance) : '' },
    { label: t('genre'), value: formData.genre },
    { label: t('email'), value: formData.email },
    { label: t('tel'), value: formData.tel },
    { label: formData.cin ? t('cin') : '', value: formData.cin }, // Conditionally render CIN label
    { label: t('scolarisation'), value: formData.scolarise },
    { label: t("niveauEtudes"), value: formData.niveauEtudes },
    { label: formData.situationActuelle ? t('situationActuelle') : '', value: formData.situationActuelle },
    { label: formData.cne ? t('cne') : '', value: formData.cne },
    { label: formData.codeMassar ? t('codeMassar') : '', value: formData.codeMassar },
  ];

  return (
    <div className="lg:h-screen lg:flex lg:items-center lg:justify-center lg:bg-gray-400">
      <div className="ml-4 mt-1 flex justify-between lg:hidden w-full">
        <BiArrowBack 
          onClick={prevStep} 
          color="black" 
          size={20} 
          className="cursor-pointer hover:opacity-70 transition mt-[6px] rtl:rotate-180"
        />
        <span className="ltr:ml-2 rtl:mr-2 mt-1">{t('back')}</span>
        <div className="ltr:ml-auto sm:ltr:mr-4 rtl:mr-auto sm:rtl:ml-4">
          <LanguageSelector />
        </div>
      </div>
      <div className="w-full h-screen lg:overflow-y-scroll lg:overflow-x-hidden lg:min-h-[450px] lg:max-h-[650px] lg:max-w-7xl lg:border lg:rounded-xl lg:min-w-[900px] xl:min-w-[1000px] bg-white lg:mx-48">
        <div className="w-full sm:mb-2 sm:mt-4 mx-4 flex flex-col justify-between">
          <div>
            <div onClick={prevStep} className='cursor-pointer -mt-2 mb-2 hidden lg:block'>
              <BiArrowBack 
                color="black" 
                size={20} 
                className="inline hover:opacity-70 transition rtl:rotate-180"
              />
              <span className="ml-2 hover:opacity-70 transition">{t('back')}</span>
            </div>
            <div className="flex items-center justify-center w-fit -ml-2 lg:ml-8">
              <Image 
                src={Logo} 
                alt={t('logoAlt')} 
                height={80} 
                width={160} 
              />
            </div>
            <h2 className="sm:text-3xl text-2xl font-medium mt-2 mb-6 text-gray-950 lg:ml-8 rtl:mr-4">{t('recapTitle')}</h2>
            <div className="lg:flex lg:justify-center lg:items-center pr-4 lg:pr-0">
              <div className="grid grid-cols-2 gap-4 lg:w-4/5 xl:overflow-hidden">
                {data.map((item, index) => (
                  <React.Fragment key={index}>
                    {item.label && (
                      <>
                        <div className="border p-2 bg-gray-100 font-semibold">{item.label}</div>
                        <div className="border p-2 bg-gray-200">{item.value}</div>
                      </>
                    )}
                  </React.Fragment>
                ))}
              </div>
            </div>
          </div>
          <button 
            onClick={() => {handleSubmit(formData)}}
            className='bg-blue-900 rounded-2xl mt-8 py-1 w-fit px-12 md:px-24 mr-4 lg:mr-[89px] xl:mr-24 max-w-sm text-white font-medium ml-auto'
          >
            {t('submitButton')}
          </button> 
          <div className="hidden lg:block">
            <LanguageSelector />
          </div>
        </div>
      </div>
    </div>
  );
}

export default Recapitulatif;
