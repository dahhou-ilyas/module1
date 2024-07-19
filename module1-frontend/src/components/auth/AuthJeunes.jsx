"use client"

import React, { useRef } from 'react'; 
import Image from "next/image";
import Link from 'next/link';
import { useRouter } from "next/navigation";
// import { jwtDecode } from 'jwt-decode';

import { zodResolver } from "@hookform/resolvers/zod"
import { useForm } from "react-hook-form"
import { z } from "zod"

import { LanguageSelector } from '@/components/LanguageSelector';
import { Input } from "@/components/ui/input"
import {
    Form,
    FormControl,
    FormDescription,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
  } from "@/components/ui/form"
  import {
    Tooltip,
    TooltipContent,
    TooltipProvider,
    TooltipTrigger,
  } from "@/components/ui/tooltip"


import Logo from "../../../public/logoJeune.png";
import Illustration from "../../../public/2862289.webp";

import { MdLiveHelp } from "react-icons/md";

import Terms from './Terms';
import CheckVerifiedEmail from './CheckVerifiedEmail';


const schema = z.object({
    identifier: z.string().min(1, "Veuillez saisir votre identifiant"),
    password: z.string()
    .min(8, 'Le mot de passe doit faire au moins 8 caractères')
    .max(16, 'Le mot de passe ne peut pas dépasser 16 caractères'),
  });


const AuthJeunes = () => {
    const router = useRouter();
    const form = useForm({
        defaultValues: {
          identifier: "",
          password: "",
        },
        resolver: zodResolver(schema),
      });
    
      const alertDialogTriggerRef = useRef(null);

    const alertDialogTriggerRef2 = useRef(null);
// fetch('http://localhost:8080/auth/login/medecins', {
        //     method: 'POST',
        //     headers: {
        //         'Content-Type': 'application/json'
        //     },
        //     body: JSON.stringify({
        //         username:data.identifier,
        //         password:data.password
        //     })
        //   })
        //   .then(response => response.json())
        //   .then(data =>{ 
        //     const decodeToken=jwtDecode(data["access-token"])
        //     if(decodeToken.claims.confirmed==false){
        //         console.log("yous must confiremd your email");
        //     }else if (decodeToken.claims.isFirstAuth==true){
        //         console.log("yous must respect rules");
        //     }else{
        //         console.log("transfere vers la page d'accue");
        //     }
        //   })
        //   .catch(error => console.error('Error:', error));
        // console.log(data);
    const onSubmit = (data) => {

        //verifier les identifiants....si tout est ok alors: 


        if (alertDialogTriggerRef.current && false) {
            // ajouter dans les conditions && jwt variable first login
            alertDialogTriggerRef.current.click();
        } else  {
            if (alertDialogTriggerRef2.current ) { // ajouter dans les conditions && email non valide
                alertDialogTriggerRef2.current.click();
            }

        else {
            router.push('/')
        }
    };
    }
    const nextStep = () => {
        router.push('/auth/firstlogin')
    }
    const envoyerEmail = () => {
        //envoyerEmailderécuperation
        //afficher Confirmation component (a faire plus tard)
    }
  return (

    <div className="lg:h-screen lg:flex lg:items-center lg:justify-center lg:bg-gray-400">
        <div className="mt-1 flex justify-between lg:hidden w-full">
            <div className="ml-auto mr-2">
                <LanguageSelector />
            </div>
        </div>
        
        <div className="lg:min-h-[550px] lg:max-w-7xl lg:border lg:rounded-3xl xl:min-w-[1000px] lg:min-w-[900px] bg-white sm:flex xl:mx-48">
            <div className="w-full md:w-1/2 flex flex-col justify-center mt-8">
                    <div className='px-4 md:px-0 md:ml-8 lg:ml-12'>
                        <div
                        className="
                            flex 
                            items-center 
                            justify-center 
                            -ml-2
                            mb-4
                        ">
                        <Image 
                        src={Logo} 
                        alt="Logo" 
                        height={200} 
                        width={250} 
                        />
                    </div>
                    <div className="flex flex-col items-center justify-center">
                        <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8">
                        
                            <FormField
                            control={form.control}
                            name="identifier"
                            render={({ field }) => (
                                <FormItem>
                                <FormLabel>Email ou CIN ou CNE ou Code Massar</FormLabel>
                                <FormControl>
                                    <Input className="sm:w-96 max-w-sm" placeholder="Email ou CIN ou CNE ou Code Massar" {...field} />
                                </FormControl>
                                
                                <FormMessage className="sm:w-96 max-w-sm" />
                                </FormItem>
                            )}
                            /> 
                            
                            <FormField
                            control={form.control}
                            name="password"
                            render={({ field }) => (
                                <FormItem>
                                <FormLabel className=" sm:inline">Mot de Passe</FormLabel>
                                <FormDescription className="hidden sm:inline border-b-2 border-blue-600 text-blue-600 cursor-pointer sm:ml-40">
                                <Link href="/forgotPassword">Mot de passe oublié?</Link>
                                </FormDescription>
                                
                                <FormControl>
                                    <Input className="sm:w-96 max-w-sm" type="password" id="password" placeholder="Mot de Passe" {...field} />
                                </FormControl>
                                
                                <FormMessage className="sm:w-96 max-w-sm" />
                                <FormDescription className="sm:hidden border-b-[1px] inline-block border-blue-600 text-blue-600 cursor-pointer">
                                <Link href="/forgotPassword">Mot de passe oublié?</Link>
                                </FormDescription>
                                </FormItem>
                            )}
                            />

                            
                            <button 
                                type="submit" 
                                className='
                                    bg-blue-900 
                                    rounded-2xl 
                                    mt-4 
                                    py-1 
                                    w-full 
                                    max-w-sm 
                                    text-white 
                                    font-medium 
                                    transform 
                                    transition 
                                    duration-300 
                                    lg:hover:scale-100
                                    lg:scale-95
                                    hover:shadow-lg
                                    hover:bg-blue-800
                                '
                                > 
                                Se Connecter
                                </button>
                            <Terms nextStep={nextStep}  alertDialogTriggerRef={alertDialogTriggerRef}/>
                            <CheckVerifiedEmail envoyerEmail={envoyerEmail}  alertDialogTriggerRef={alertDialogTriggerRef2}/>
                        </form>
                        </Form>
                     
                    </div>
                    <Link href="/register/jeunes" >
                        <h4 className="text-xs text-center text-gray-700 mt-4"> 
                            <div className="hidden sm:inline">
                        <TooltipProvider  >   <Tooltip >
                    <TooltipTrigger>
                        <MdLiveHelp className='inline mr-2 sm:scale-150'/>
                        </TooltipTrigger>
                            <TooltipContent side="bottom" className="mt-1 ml-32">
                            <p>Lorem ipsum dolor sit amet consectetur, adipisicing elit. Vero esse quia aut corporis natus ex deleniti laudantium</p>
                            </TooltipContent>
                        </Tooltip>
                        </TooltipProvider> </div>Vous n'avez pas de compte ?<span className=' font-semibold border-b-2 border-gray-700 cursor-pointer'> Inscrivez-vous </span></h4>  </Link>


                        <p className='text-gray-600 sm:hidden mt-8 mb-1 text-xs mx-auto'>Lorem ipsum dolor sit amet consectetur, adipisicing elit. Vero esse quia aut corporis natus ex deleniti laudantium</p>
                    {/* <TooltipProvider>
  <Tooltip>
    <TooltipTrigger>Hover</TooltipTrigger>
    <TooltipContent>
      <p>Add to library</p>
    </TooltipContent>
  </Tooltip>
</TooltipProvider> */}

                </div>    
                <div className="hidden lg:block ml-3 mt-auto">
                    <LanguageSelector />
                </div>
            </div>
            <div className="hidden md:block md:w-1/2 md:mt-20 lg:mt-28 ">
            
            <div className="md:animate-bounce-slow">
                <Image 
                        src={Illustration} 
                        alt="Illustration" 
                        layout="responsive"
                        height={1000} 
                        width={450} 
                    /></div>
            </div>
            
        </div>
        </div>
    )
    }

export default AuthJeunes;