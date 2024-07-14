"use client"
import { useState } from 'react';
import InformationsActivites from '@/components/auth/register/InformationsActivites';
import NameForm from '@/components/auth/register/NameForm';
import EmailForm from '@/components/auth/register/EmailForm';
import PasswordForm from '@/components/auth/register/PasswordForm';



const RegisterProfessionnelsForm = () => {
  const [step, setStep] = useState(1);
  const [formData, setFormData] = useState({
    nom: '',
    prenom: '',
    cin: "",
    inpe: "",
    email: '',
    tel: '',
    password: "",
  });

  const nextStep = () => setStep(step + 1);
  const prevStep = () => setStep(step - 1);
  
  
  const handleSubmit = (values) => {
    console.log('Form Data:', values);
    fetch('http://localhost:8080/register/professionnelsantes', {
      method: 'POST',
      headers: {
          'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        user:{
          nom:formData.nom,
          prenom:formData.prenom,
          mail:formData.email,
          numTele:formData.tel.replace(/^0/, "+212"),
          password:formData.password,
        },
        cin:formData.cin,
        inpe:formData.inpe
      })
    })
    .then(response => response.json())
    .then(data => console.log('Success:', data))
    .catch(error => console.error('Error:', error));
  };

   
  const renderStep = () => {
 
    switch (step) {
      case 1:
        return <NameForm nextStep={nextStep} setFormData={setFormData} formData={formData} bgColor={"green"} buttonColor={"green"}/>;
      case 2:
        return <InformationsActivites nextStep={nextStep} prevStep={prevStep} setFormData={setFormData} formData={formData} />;
      case 3:
        return <EmailForm nextStep={nextStep} prevStep={prevStep} setFormData={setFormData} formData={formData} bgColor={"green"} buttonColor={"green"} />;
      
      default:
        return <PasswordForm nextStep={handleSubmit} prevStep={prevStep} setFormData={setFormData} formData={formData} bgColor={"green"} buttonColor={"green"} />;
    }
  };

  return (
    <div>
      {renderStep()}
    </div>
  );
};

export default RegisterProfessionnelsForm;