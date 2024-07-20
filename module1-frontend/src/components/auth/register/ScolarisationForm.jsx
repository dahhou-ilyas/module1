import * as React from "react";
import Layout from "@/components/auth/Layout";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";

import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { useTranslations } from 'next-intl';

const Fields = ({ setFormData, nextStep }) => {
  const t = useTranslations('ScolarisationForm');

  const schema = z.object({
    scolarise: z.string().nonempty(t("scolariseError")),
    niveauEtudes: z.string().nonempty(t("niveauEtudesError")),
    enActivite: z.string().optional(),
  });

  const form = useForm({
    defaultValues: {
      scolarise: "",
      niveauEtudes: "",
      enActivite: "", 
    },
    resolver: zodResolver(schema),
  });

  const { handleSubmit, control, formState: { errors }, watch } = form;
  const scolariseValue = watch("scolarise");

  const onSubmit = (data) => {
    if (data.scolarise === "non" && !data.enActivite) {
      form.setError("enActivite", {
        type: "manual",
        message: t("enActiviteError"),
      });
      return;
    }

    setFormData((prevFormData) => ({
      ...prevFormData,
      scolarise: data.scolarise,
      niveauEtudes: data.niveauEtudes,
      enActivite: data.enActivite || "",
    }));
    nextStep();
  };

  return (
    <div className="sm:mt-8">
      <Form {...form}>
        <form onSubmit={handleSubmit(onSubmit)} className="space-y-8">
          <div className="w-full flex flex-col justify-between gap-4">
            <FormField
              control={control}
              name="scolarise"
              render={({ field }) => (
                <FormItem className="space-y-3">
                  <FormLabel>{t("scolariseLabel")}</FormLabel>
                  <FormControl>
                    <RadioGroup
                      onValueChange={field.onChange}
                      defaultValue={field.value}
                      className="flex flex-col space-y-1"
                    >
                      <FormItem className="flex items-center space-x-3 space-y-0">
                        <FormControl>
                          <RadioGroupItem value="oui" />
                        </FormControl>
                        <FormLabel className="font-normal">{t("oui")}</FormLabel>
                      </FormItem>
                      <FormItem className="flex items-center space-x-3 space-y-0">
                        <FormControl>
                          <RadioGroupItem value="non" />
                        </FormControl>
                        <FormLabel className="font-normal">{t("non")}</FormLabel>
                      </FormItem>
                    </RadioGroup>
                  </FormControl>
                  <FormMessage>{errors.scolarise?.message}</FormMessage>
                </FormItem>
              )}
            />

            {scolariseValue === "non" && (<>
              <FormField
                control={control}
                name="niveauEtudes"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Dernier Niveau d'Études</FormLabel>
                    <Select onValueChange={field.onChange} defaultValue={field.value}>
                      <SelectTrigger>
                        <SelectValue placeholder="Sélectionnez votre niveau d'études actuel" />
                      </SelectTrigger>
                      <SelectContent>
                        <SelectItem value="AUCUN">Aucun</SelectItem>
                        <SelectItem value="PRIMAIRE">Primaire</SelectItem>
                        <SelectItem value="SECONDAIRE">Secondaire</SelectItem>
                        <SelectItem value="SUPERIEUR">Supérieure</SelectItem>
                      </SelectContent>
                    </Select>
                    <FormMessage>{errors.niveauEtudes?.message}</FormMessage>
                  </FormItem>
                )}
              />
              

                <FormField
                  control={control}
                  name="enActivite"
                  render={({ field }) => (
                    <FormItem className="space-y-3">
                      <FormLabel>{t("enActiviteLabel")}</FormLabel>
                      <FormControl>
                        <RadioGroup
                          onValueChange={field.onChange}
                          defaultValue={field.value}
                          className="flex flex-col space-y-1"
                        >
                          <FormItem className="flex items-center space-x-3 space-y-0">
                            <FormControl>
                              <RadioGroupItem value="oui" />
                            </FormControl>
                            <FormLabel className="font-normal">{t("oui")}</FormLabel>
                          </FormItem>
                          <FormItem className="flex items-center space-x-3 space-y-0">
                            <FormControl>
                              <RadioGroupItem value="non" />
                            </FormControl>
                            <FormLabel className="font-normal">{t("non")}</FormLabel>
                          </FormItem>
                        </RadioGroup>
                      </FormControl>
                      <FormMessage>{errors.enActivite?.message}</FormMessage>
                    </FormItem>
                  )}
                />
              </>
            )}

            {scolariseValue === "oui" && (
                <FormField
                  control={control}
                  name="niveauEtudes"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Niveau d'Études Actuel</FormLabel>
                      <Select onValueChange={field.onChange} defaultValue={field.value}>
                        <SelectTrigger>
                          <SelectValue placeholder="Sélectionnez votre niveau d'études actuel" />
                        </SelectTrigger>
                        <SelectContent>
                          <SelectItem value="PRIMAIRE">Primaire</SelectItem>
                          <SelectItem value="SECONDAIRE">Secondaire</SelectItem>
                          <SelectItem value="SUPERIEUR">Supérieure</SelectItem>
                        </SelectContent>
                      </Select>
                      <FormMessage>{errors.niveauEtudes?.message}</FormMessage>
                    </FormItem>
                  )}
                />
            )}

            <button type="submit" className="bg-blue-900 rounded-2xl mt-8 py-1 px-6 w-fit text-white font-medium ml-auto">
              {t("submitButton")}
            </button>
          </div>
        </form>
      </Form>
    </div>
  ); 
};

const ScolarisationForm = ({ setFormData, prevStep, nextStep }) => {
  const t = useTranslations('ScolarisationForm');

  return (
    <Layout
      title={t("layoutTitle")}
      subtitle={t("layoutSubtitle")}
      fields={<Fields setFormData={setFormData} nextStep={nextStep} />}
      prevStep={prevStep}
    />
  );
};

export default ScolarisationForm;
