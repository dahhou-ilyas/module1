"use client"
import { useState } from 'react';
import PasswordForm from '@/components/auth/register/PasswordForm';
import EmailRecovery from '@/components/auth/recoverPassword/EmailRecovery';
import VerifyToken from '@/components/auth/recoverPassword/VerifyToken';

const ForgotPassword = () => {

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

export default ForgotPassword;