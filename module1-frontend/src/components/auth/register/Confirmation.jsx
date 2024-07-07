import Layout from "@/components/auth/register/Layout"


const Confirmation = ({prevStep}) => {
    return ( 
    <Layout 
      title={"Consulter votre boite mail"} 
      subtitle={"Veuillez consulter votre boîte mail pour vérifier votre compte."} 
      prevStep={prevStep}
      />
     );
}
 
export default Confirmation;


