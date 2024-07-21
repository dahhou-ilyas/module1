"use client"

import React, { useRef, useState } from 'react'; 
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

import { useTranslations } from "next-intl";

const AuthJeunes = () => {
    const t = useTranslations("AuthJeunes");
    const [token, setToken] = useState({});
    const [accesToken, setAccesToken] = useState('');
    const router = useRouter();
    
    const schema = z.object({
        identifier: z.string().min(1, t("identifierError")),
        password: z.string()
        .min(8, t("passwordMinError"))
        .max(16, t("passwordMaxError")),
    });

    const form = useForm({
        defaultValues: {
          identifier: "",
          password: "",
        },
        resolver: zodResolver(schema),
      });
    
      const alertDialogTriggerRef = useRef(null);
      const alertDialogTriggerRef2 = useRef(null);

    const onSubmit = (data) => {
        console.log("zaza z  ", data);
        router.push('/')
    }
    const tohomePage = () => {
        router.push('/')
    }
    const nextStep = () => {
        router.push('/auth/firstlogin?token=' + accesToken)
    }
    const envoyerEmail = () => {
        // Email sending logic here
    }

    return (
        <div className="lg:h-screen lg:flex lg:items-center lg:justify-center lg:bg-gray-400">
            <div className="mt-1 flex justify-between lg:hidden w-full">
                <div className="ml-auto mr-2">
                    <LanguageSelector />
                </div>
            </div>
            
            <div className="lg:min-h-[550px] lg:max-w-7xl lg:border lg:rounded-3xl xl:min-w-[1000px] lg:min-w-[900px] bg-white sm:flex xl:mx-48">
                <div className="w-full md:w-1/2 flex flex-col justify-center mt-8 sm:rtl:mr-8">
                    <div className='px-4 md:px-0 md:ml-8 lg:ml-12'>
                        <div className="flex items-center justify-center -ml-2 mb-4">
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
                                                <FormLabel>{t("identifierLabel")}</FormLabel>
                                                <FormControl>
                                                    <Input className="w-80 sm:w-96 max-w-sm" placeholder={t("identifierPlaceholder")} {...field} />
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
                                                <FormLabel className="sm:inline">{t("passwordLabel")}</FormLabel>
                                                <FormDescription className="hidden sm:inline border-b-2 border-blue-600 text-blue-600 cursor-pointer ltr:sm:ml-40 rtl:sm:mr-60">
                                                    <Link href="/forgotPassword">{t("forgotPassword")}</Link>
                                                </FormDescription>
                                                <FormControl>
                                                    <Input className="w-80 sm:w-96 max-w-sm" type="password" id="password" placeholder={t("passwordPlaceholder")} {...field} />
                                                </FormControl>
                                                <FormMessage className="sm:w-96 max-w-sm" />
                                                <FormDescription className="sm:hidden border-b-[1px] inline-block border-blue-600 text-blue-600 cursor-pointer">
                                                    <Link href="/forgotPassword">{t("forgotPassword")}</Link>
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
                                        {t("loginButton")}
                                    </button>
                                    <Terms nextStep={nextStep} alertDialogTriggerRef={alertDialogTriggerRef} />
                                    <CheckVerifiedEmail envoyerEmail={envoyerEmail} alertDialogTriggerRef={alertDialogTriggerRef2} />
                                </form>
                            </Form>
                        </div>
                        <Link href="/register/jeunes">
                            <h4 className="text-xs text-center text-gray-700 mt-4"> 
                                <div className="hidden sm:inline">
                                    <TooltipProvider>   
                                        <Tooltip>
                                            <TooltipTrigger>
                                                <MdLiveHelp className='inline ltr:mr-2 rtl:ml-2 sm:scale-150'/>
                                            </TooltipTrigger>
                                            <TooltipContent side="bottom" className="mt-1 ml-32">
                                                <p>{t("tooltipContent")}</p>
                                            </TooltipContent>
                                        </Tooltip>
                                    </TooltipProvider> 
                                </div>
                                {t("noAccount")} <span className='font-semibold border-b-2 border-gray-700 cursor-pointer'>{t("register")}</span>
                            </h4>
                        </Link>
                        <p className='text-gray-600 sm:hidden mt-8 mb-1 text-xs text-center'>{t("mobileTooltipContent")}</p>
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
                        />
                    </div>
                </div>
            </div>
        </div>
    );
}

export default AuthJeunes;
