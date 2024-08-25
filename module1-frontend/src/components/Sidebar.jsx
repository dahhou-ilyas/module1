/* eslint-disable react/no-unescaped-entities */

import "@/assets/css/font-awesome.min.css";
import Link from "next/link";
import Image from "next/image";

import { useTranslations } from 'next-intl';
import { useRouter } from 'next/navigation';

import { dashboard, doctor, logout, menuicon10, menuicon08 } from "./imagepath";
import Scrollbars from "react-custom-scrollbars-2";
import { useEffect } from "react";
//import "bootstrap/dist/js/bootstrap.bundle.min.js";

const Sidebar = (props) => {
  const t = useTranslations('Navbar');
  const router = useRouter();

  useEffect(()=>{
    require("bootstrap/dist/js/bootstrap.bundle.min.js")
  },[])
  const expandMenu = () => {
    document.body.classList.remove("expand-menu");
  };

  const expandMenuOpen = () => {
    document.body.classList.add("expand-menu");
  };
  const handleLogout = () => {
    console.log("object");
    localStorage.removeItem('access-token');
    router.push('/auth/jeunes');
  };
  return (
    <div className="sidebar" id="sidebar">
      <Scrollbars
        autoHide
        autoHideTimeout={1000}
        autoHideDuration={200}
        autoHeight
        autoHeightMin={0}
        autoHeightMax="95vh"
        thumbMinSize={30}
        universal={false}
        hideTracksWhenNotNeeded={true}
      >
        <div className="sidebar-inner slimscroll">
          <div
            id="sidebar-menu"
            className="sidebar-menu"
            onMouseLeave={expandMenu}
            onMouseOver={expandMenuOpen}
          >
            <ul>
              <li className="menu-title rtl:mr-4 ltr:ml-4">e-ESJ</li>
              <li className="submenu">
                <Link
                  className={
                    props?.activeClassName === "dashboard" ? "active" : ""
                  }
                  href="/TeleExpertise"
                >
                  <span className="menu-side">
                    <Image src={dashboard} alt="" />
                  </span>{" "}
                  <span> {t('home')} </span>
                </Link>
              </li>
              <li className="submenu">
                <Link
                  className={
                    props?.activeClassName === "doctors" ? "active" : ""
                  }
                  href="/TeleExpertise/Medecins"
                >
                  <span className="menu-side">
                    <Image src={doctor} alt="" />
                  </span>{" "}
                  <span> {t('medicalRecord')} </span>
                </Link>
              </li>
              <li>
                <Link
                  className={
                    props?.activeClassName === "discussions" ? "active" : ""
                  }
                  href="/TeleExpertise/Discussions"
                >
                  <span className="menu-side">
                    <Image src={menuicon08} alt="" />
                  </span>{" "}
                  <span>{t('healthEducation')}</span>
                </Link>
              </li>
              <li>
                <Link
                  className={props?.activeClassName === "chat" ? "active" : ""}
                  href="/TeleExpertise/Chat"
                >
                  <span className="menu-side">
                    <Image src={menuicon10} alt="" />
                  </span>{" "}
                  <span>{t('psychTests')}</span>
                </Link>
              </li>
              
              <li>
                <Link
                  className={
                    props?.activeClassName === "parametres" ? "active" : ""
                  }
                  href="/"
                >
                  <span className="menu-side" onClick={handleLogout}>
                    <Image src={logout} alt="" />
                  </span>{" "}
                  <span>{t("logout")}</span>
                </Link>
              </li>
            </ul>
          </div>
        </div>
      </Scrollbars>
    </div>
  );
};

export default Sidebar;
