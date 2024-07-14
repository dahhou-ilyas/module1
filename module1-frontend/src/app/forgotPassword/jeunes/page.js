"use client"
import { useState } from 'react';
import NameForm from '@/components/auth/register/NameForm';
import BirthDateForm from '@/components/auth/register/BirthDateForm';
import EmailForm from '@/components/auth/register/EmailForm';
import ScolarisationForm from '@/components/auth/register/ScolarisationForm';
import CodeMassarForm from '@/components/auth/register/CodeMassarForm';
import CneForm from '@/components/auth/register/CneForm';
import PasswordForm from '@/components/auth/register/PasswordForm';
import Recapitulatif from '@/components/auth/register/Recapitulatif';
import Confirmation from '@/components/auth/register/Confirmation';
import CinForm from '@/components/auth/register/CinForm';
import EmailRecovery from '@/components/auth/recoverPassword/EmailRecovery';
import VerifyToken from '@/components/auth/recoverPassword/VerifyToken';

const ForgotPasswordJeunes = () => {

  const [step, setStep] = useState(1);
  

  const nextStep = () => setStep(step + 1);
  
  const sendPasswordResetToken = (email) => {
    

    nextStep();
  }

  const verifyToken = (token) => {

    nextStep();
  }
  const resetPassword = (values) => {

    nextStep();
  };

 
  const renderStep = () => {

    switch (step) {
      case 1:
        return <EmailRecovery nextStep={sendPasswordResetToken} />;
      case 2:
        return <VerifyToken nextStep={verifyToken} />;
      default:
        return <PasswordForm nextStep={resetPassword}/>;
    }
  };

  return (
    <div>
      {renderStep()}
    </div>
  );
};

export default ForgotPasswordJeunes;