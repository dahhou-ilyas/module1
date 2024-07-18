import Image from "next/image";

import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
    AlertDialogTrigger,
  } from "@/components/ui/alert-dialog"

import emailIcon from "../../../public/image.png"

const CheckVerifiedEmail = ({alertDialogTriggerRef, envoyerEmail}) => {

    return ( <AlertDialog>
        <AlertDialogTrigger asChild>
            <button ref={alertDialogTriggerRef}></button>
        </AlertDialogTrigger>
        <AlertDialogContent>
            <AlertDialogHeader>
                <div className="flex items-center justify-center flex-col mt-4">
                <Image 
                
                src={emailIcon} 
                alt="email icon" 
                height={120} 
                width={120} 
                
                />
                <AlertDialogTitle className="mt-8 text-2xl text-gray-800">Confirmez votre adresse e-mail</AlertDialogTitle></div>
                <AlertDialogDescription className="mb-2 text-gray-600">
                Votre adresse e-mail n'est pas encore vérifiée. Veuillez cliquer sur le bouton "Vérifier mon e-mail" et consulter votre boîte de réception pour compléter le processus de vérification.
                </AlertDialogDescription>
            </AlertDialogHeader>
            <AlertDialogFooter className="mt-4">
                <AlertDialogCancel>Annuler</AlertDialogCancel>
                <AlertDialogAction onClick={envoyerEmail} className="bg-blue-950 hover:bg-blue-700">Vérifier mon e-mail</AlertDialogAction>
            </AlertDialogFooter>
        </AlertDialogContent>
    </AlertDialog>);
}
 
export default CheckVerifiedEmail;