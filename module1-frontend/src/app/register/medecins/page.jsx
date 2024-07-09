"use client"

import InformationsActivites from '@/components/auth/register/InformationsActivites';
import NameForm from '@/components/auth/register/NameForm';
import EmailForm from '@/components/auth/register/EmailForm';
import PasswordForm from '@/components/auth/register/PasswordForm';
import { useState } from 'react';
import InformationsMedecin from '@/components/auth/register/InformationsMedecin';


const RegisterMedecinsForm = () => {
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
  };

   
  const renderStep = () => {
 
    switch (step) {
      case 1:
        return <NameForm nextStep={nextStep} setFormData={setFormData} formData={formData}/>;
      case 2:
        return <InformationsActivites nextStep={nextStep} prevStep={prevStep} setFormData={setFormData} formData={formData} medecin={true} />;
      case 3:
        return <EmailForm nextStep={nextStep} prevStep={prevStep} setFormData={setFormData} formData={formData} />;
      case 4:
        return <InformationsMedecin nextStep={nextStep} prevStep={prevStep} setFormData={setFormData} formData={formData} />;
      default:
        return <PasswordForm nextStep={handleSubmit} prevStep={prevStep} setFormData={setFormData} formData={formData} />;
    }
  };

  return (
    <div>
      {renderStep()}
    </div>
  );
};

export default RegisterMedecinsForm;