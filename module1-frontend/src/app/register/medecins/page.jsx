"use client"

import InformationsActivites from '@/components/auth/register/InformationsActivites';
import NameForm from '@/components/auth/register/NameForm';
import EmailForm from '@/components/auth/register/EmailForm';
import PasswordForm from '@/components/auth/register/PasswordForm';
import { useState } from 'react';
import InformationsMedecin from '@/components/auth/register/InformationsMedecin';
import { useRouter } from 'next/navigation';
import Confirmation from '@/components/auth/register/Confirmation';


const RegisterMedecinsForm = () => {
  const router = useRouter()
  const [step, setStep] = useState(1);
  const [formData, setFormData] = useState({
    nom: '',
    prenom: '',
    cin: "",
    inpe: "",
    ppr: "",
    email: '',
    tel: '',
    medecinESJ: "",
    medecinGeneraliste: "",
    specialite: "",
    password: "",
  });

  const nextStep = () => setStep(step + 1);
  const prevStep = () => setStep(step - 1);
  
  
  const handleSubmit = (values) => {
    console.log('Form Data:', values);
    // fetch('http://localhost:8080/register/medecins', {
    //   method: 'POST',
    //   headers: {
    //       'Content-Type': 'application/json'
    //   },
    //   body: JSON.stringify({
    //     appUser:{
    //       nom:formData.nom,
    //       prenom:formData.prenom,
    //       mail:formData.email,
    //       numTele:formData.tel.replace(/^0/, "+212"),
    //       password:formData.password,
    //     },
    //     cin:formData.cin,
    //     inpe:formData.inpe,
    //     ppr:formData.ppr,
    //     estMedcinESJ:(formData.medecinESJ=="oui") ? true:false,
    //     estGeneraliste:(formData.medecinGeneraliste=="oui")? true:false,
    //     specialite:formData.specialite
    //   })
    // })
    // .then(response => response.json())
    // .then(data => nextStep())
    // .catch(error => console.error('Error:', error));

    nextStep()
  };

   
  const renderStep = () => {
 
    switch (step) {
      case 1:
        return <NameForm nextStep={nextStep} setFormData={setFormData} formData={formData}  />;
      case 2:
        return <InformationsActivites nextStep={nextStep} prevStep={prevStep} setFormData={setFormData} formData={formData} medecin={true} />;
      case 3:
        return <EmailForm nextStep={nextStep} prevStep={prevStep} setFormData={setFormData} formData={formData} />;
      case 4:
        return <InformationsMedecin nextStep={nextStep} prevStep={prevStep} setFormData={setFormData} formData={formData} />;
      case 5:
        return <PasswordForm nextStep={handleSubmit} prevStep={prevStep} setFormData={setFormData} formData={formData}  />;
      case 6:
        return <Confirmation />;
    }
  };

  return (
    <div>
      {renderStep()}
    </div>
  );
};

export default RegisterMedecinsForm;