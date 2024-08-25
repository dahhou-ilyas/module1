"use client";
import "@/assets/css/style.css";
import Link from "next/link";
import { useEffect } from "react";
import {
  logo,
  baricon,
  baricon1,
  user06,
  noteicon1,
} from "./imagepath";
import Image from "next/image";

import { PiSignOut } from "react-icons/pi";
import { IoCheckmark } from "react-icons/io5";
import { RxCross1 } from "react-icons/rx";
import { FaRegUser } from "react-icons/fa6";

import { useTranslations, useLocale } from 'next-intl';
import { useRouter, usePathname } from 'next/navigation';
import { useTransition } from 'react';
const Header = ({ user }) => {
  const t = useTranslations('Navbar');
  const locale = useLocale();
  const router = useRouter();
  const pathname = usePathname();

  const [isPending, startTransition] = useTransition();

  const onSelectChange = (value) => {
    const segments = pathname.split('/').filter(Boolean);
    segments[0] = value;
    const newPathname = `/${segments.join('/')}`;

    startTransition(() => {
      router.replace(newPathname);
    });
  };

  // Extract prenom and capitalize it
  const firstName = user?.claims?.prenom
    ? user.claims.prenom.charAt(0).toUpperCase() + user.claims.prenom.slice(1).toLowerCase()
    : "";
  // const messages = [
  //   {
  //     id: 1,
  //     avatar: "R",
  //     author: "Richard Miles",
  //     time: "12:28 AM",
  //     content: "Lorem ipsum dolor sit amet, consectetur adipiscing",
  //     href: "/",
  //   },
  //   {
  //     id: 2,
  //     avatar: "J",
  //     author: "John Doe",
  //     time: "1 Aug",
  //     content: "Lorem ipsum dolor sit amet, consectetur adipiscing",
  //     href: "/",
  //     newMessage: true,
  //   },
  //   {
  //     id: 3,
  //     avatar: "T",
  //     author: "Tarah Shropshire",
  //     time: "12:28 AM",
  //     content: "Lorem ipsum dolor sit amet, consectetur adipiscing",
  //     href: "/",
  //   },
  //   {
  //     id: 4,
  //     avatar: "M",
  //     author: "Mike Litorus",
  //     time: "12:28 AM",
  //     content: "Lorem ipsum dolor sit amet, consectetur adipiscing",
  //     href: "/",
  //   },
  //   {
  //     id: 5,
  //     avatar: "C",
  //     author: "Catherine Manseau",
  //     time: "12:28 AM",
  //     content: "Lorem ipsum dolor sit amet, consectetur adipiscing",
  //     href: "/",
  //   },
  //   {
  //     id: 6,
  //     avatar: "D",
  //     author: "Domenic Houston",
  //     time: "12:28 AM",
  //     content: "Lorem ipsum dolor sit amet, consectetur adipiscing",
  //     href: "/",
  //   },
  //   {
  //     id: 7,
  //     avatar: "B",
  //     author: "Buster Wigton",
  //     time: "12:28 AM",
  //     content: "Lorem ipsum dolor sit amet, consectetur adipiscing",
  //     href: "/",
  //   },
  //   {
  //     id: 8,
  //     avatar: "R",
  //     author: "Rolland Webber",
  //     time: "12:28 AM",
  //     content: "Lorem ipsum dolor sit amet, consectetur adipiscing",
  //     href: "/",
  //   },
  //   {
  //     id: 9,
  //     avatar: "C",
  //     author: "Claire Mapes",
  //     time: "12:28 AM",
  //     content: "Lorem ipsum dolor sit amet, consectetur adipiscing",
  //     href: "/",
  //   },
  //   {
  //     id: 10,
  //     avatar: "M",
  //     author: "Melita Faucher",
  //     time: "12:28 AM",
  //     content: "Lorem ipsum dolor sit amet, consectetur adipiscing",
  //     href: "/",
  //   },
  //   {
  //     id: 11,
  //     avatar: "J",
  //     author: "Jeffery Lalor",
  //     time: "12:28 AM",
  //     content: "Lorem ipsum dolor sit amet, consectetur adipiscing",
  //     href: "/",
  //   },
  //   {
  //     id: 12,
  //     avatar: "L",
  //     author: "Loren Gatlin",
  //     time: "12:28 AM",
  //     content: "Lorem ipsum dolor sit amet, consectetur adipiscing",
  //     href: "/",
  //   },
  //   {
  //     id: 13,
  //     avatar: "T",
  //     author: "Tarah Shropshire",
  //     time: "12:28 AM",
  //     content: "Lorem ipsum dolor sit amet, consectetur adipiscing",
  //     href: "/",
  //   },
  // ];

  const notifications = [
    {
      id: 1,
      avatar: "V",
      user: "John Doe",
      action: "added new task",
      task: "Patient appointment booking",
      time: "4 mins ago",
      href: "/",
    },
    {
      id: 2,
      avatar: "V",
      user: "Tarah Shropshire",
      action: "changed the task name",
      task: "Appointment booking with payment gateway",
      time: "6 mins ago",
      href: "/",
    },
    {
      id: 3,
      avatar: "L",
      user: "Misty Tison",
      action: "added",
      task: "Doctor available module",
      additional: ["Domenic Houston", "Claire Mapes"],
      time: "8 mins ago",
      href: "/",
    },
    {
      id: 4,
      avatar: "G",
      user: "Rolland Webber",
      action: "completed task",
      task: "Patient and Doctor video conferencing",
      time: "12 mins ago",
      href: "/",
    },
    {
      id: 5,
      avatar: "V",
      user: "Bernardo Galaviz",
      action: "added new task",
      task: "Private chat module",
      time: "2 days ago",
      href: "/",
    },
  ];
  useEffect(() => {
    require("bootstrap/dist/js/bootstrap.bundle.min.js");
  }, []);

  const handlesidebar = () => {
    document.body.classList.toggle("mini-sidebar");
  };

  const handlesidebarmobilemenu = () => {
    document.body.classList.toggle("slide-nav");
    document.getElementsByTagName("html")[0].classList.toggle("menu-opened");
    /*document
      .getElementsByClassName("sidebar-overlay")[0]
      .classList.toggle("opened");*/
  };

  const openDrawer = () => {
    const div = document.querySelector(".main-wrapper");
    if (div?.className?.includes("open-msg-box")) {
      div?.classList?.remove("open-msg-box");
    } else {
      div?.classList?.add("open-msg-box");
    }
  };

  useEffect(() => {
    const handleClick = () => {
      if (!document.fullscreenElement) {
        document.documentElement.requestFullscreen();
      } else {
        if (document.exitChaimaAitAliFullscreen) {
          document.exitFullscreen();
        }
      }
    };

    const maximizeBtn = document.querySelector(".win-maximize");
    // maximizeBtn.addEventListener('click', handleClick);

    return () => {
      // maximizeBtn.removeEventListener('click', handleClick);
    };
  }, []);
  const handleLogout = () => {
    console.log("object");
    localStorage.removeItem('access-token');
    router.push('/auth/jeunes');
  };
  return (
    <div className="main-wrapper">
      <div className="header">
        <div className="header-left">
          <Link href="/" className="logo">
            <Image src={logo} width={35} height={35} alt="" />{" "}
            <span className="rtl:hidden">e-ESJ</span>
          </Link>
        </div>
        <Link
          href="#"
          id="toggle_btn"
          onClick={handlesidebar}
          style={{ marginTop: "28px" }}
        >
          <Image src={baricon} alt="" />
        </Link>
        <Link
          href="#"
          id="mobile_btn"
          className="mobile_btn float-start"
          onClick={handlesidebarmobilemenu}
        >
          <Image src={baricon1} alt="" />
        </Link>

        <ul className="nav user-menu float-end" dir="ltr">
          <li className="nav-item dropdown d-none d-sm-block">
            <Link
              href="/"
              className="dropdown-toggle nav-link"
              data-bs-toggle="dropdown"
            >
              <Image style={{ marginTop: "25px" }} src={noteicon1} alt="" />
              <span className="pulse" />
            </Link>
            <div className="dropdown-menu notifications">
              <div className="topnav-dropdown-header">
                <span>Notifications</span>
              </div>
              <div className="drop-scroll">
                <ul className="notification-list">
                  {notifications.map((notification) => (
                    <li key={notification.id} className="notification-message">
                      <Link href={notification.href}>
                        <div className="media">
                          <span className="avatar">{notification.avatar}</span>
                          <div className="media-body">
                            <p className="noti-details">
                              <span className="noti-title">
                                {notification.user}
                              </span>{" "}
                              {notification.action}{" "}
                              {notification.additional &&
                                notification.additional.map((name, index) => (
                                  <span key={index} className="noti-title">
                                    {name}
                                    {index <
                                      notification.additional.length - 1 &&
                                      " and "}
                                  </span>
                                ))}
                              {notification.task && (
                                <span className="noti-title">
                                  {notification.task}
                                </span>
                              )}
                            </p>
                            <p className="noti-time">
                              <span className="notification-time">
                                {notification.time}
                              </span>
                            </p>
                          </div>
                        </div>
                      </Link>
                    </li>
                  ))}
                </ul>
              </div>
              <div className="topnav-dropdown-footer">
                <Link href="/">View all Notifications</Link>
              </div>
            </div>
          </li>
          {/* <li className="nav-item dropdown d-none d-sm-block">
            <Link
              href="/"
              onClick={openDrawer}
              id="open_msg_box"
              className="hasnotifications nav-link"
            >
              <Image src={noteicon} alt="" />
              <span className="pulse" />{" "}
            </Link>
          </li> */}
          <li className="nav-item dropdown has-arrow user-profile-list">
            <Link
              href="/"
              className="dropdown-toggle nav-link user-link"
              data-bs-toggle="dropdown"
            >
              <div className="user-names">
                <h5 className="hidden md:block">{firstName || t('username')}</h5>
              </div>
              {/* <span className="user-img">
                <Image src={user06} alt="Admin"/>
              </span> */}
              <Image src={user06} alt="Admin" className="user-img" />
            </Link>
            <div className="dropdown-menu">
              
              
                  <li key={t('profile')} className="px-4 py-2 hover:bg-zinc-100 flex items-center space-x-2">
                    <span className="rtl:ml-1"><FaRegUser /></span>
                    <button 
                      className="block text-sm font-medium text-gray-700"
                    >
                      {t('profile')}
                    </button>
                  </li>
                  
              <li className="hover:bg-zinc-100 flex items-center space-x-2 ltr:ml-6 rtl:pr-6">
                  {locale === 'fr' ? <span className="rtl:ml-1"><IoCheckmark /></span> : <span className="rtl:ml-1"><RxCross1 /></span>}
                  <button onClick={() => onSelectChange('fr')} className="block text-sm text-gray-700 font-medium">
                    {t('languageFr')}
                  </button>
                </li>
                <li className=" py-2 hover:bg-zinc-100 flex items-center space-x-2 ltr:ml-6 rtl:pr-6">
                  {locale === 'ar' ? <span className="rtl:ml-1"><IoCheckmark /></span> : <span className="rtl:ml-1"><RxCross1 /></span>}
                  <button onClick={() => onSelectChange('ar')} className="block text-sm font-medium text-gray-700 ">
                    {t('languageAr')}
                  </button>
                </li>
                <li key={t('logout')} className="ltr:pl-6 rtl:pr-6 pb-2 hover:bg-zinc-100 flex items-center space-x-2">
                    <span className="rtl:ml-1"><PiSignOut /></span>
                    <button 
                      className="block text-sm font-medium text-gray-700"
                      onClick={handleLogout}
                    >
                      {t('logout')}
                    </button>
                  </li>
            </div>
          </li>
          {/* <li className="nav-item ">
            <Link href="/Parametres" className="hasnotifications nav-link">
              <Image src={settingicon01} alt="" />{" "}
            </Link>
          </li> */}
        </ul>
        <div className="dropdown mobile-user-menu float-end">
          <Link
            href="/"
            className="dropdown-toggle"
            data-bs-toggle="dropdown"
            aria-expanded="false"
          >
            <i className="fa-solid fa-ellipsis-vertical" />
          </Link>
          <div className="dropdown-menu dropdown-menu-end">
            <Link href="/" className="dropdown-item">
              My Profile
            </Link>
            <Link href="/" className="dropdown-item">
              Edit Profile
            </Link>
            <Link href="/" className="dropdown-item">
              Settings
            </Link>
            <Link href="/" className="dropdown-item">
              Logout
            </Link>
          </div>
        </div>
      </div>

      {/* messages */}
      {/*
      <div className="notification-box">
        <div className="msg-sidebar notifications msg-noti">
          <div className="topnav-dropdown-header">
            <span>Messages</span>
          </div>
          <div className="drop-scroll msg-list-scroll" id="msg_list">
            <ul className="list-box">
              {messages.map((message) => (
                <li key={message.id}>
                  <Link href={message.href}>
                    <div
                      className={`list-item ${
                        message.newMessage ? "new-message" : ""
                      }`}
                    >
                      <div className="list-left">
                        <span className="avatar">{message.avatar}</span>
                      </div>
                      <div className="list-body">
                        <span className="message-author">{message.author}</span>
                        <span className="message-time">{message.time}</span>
                        <div className="clearfix"></div>
                        <span className="message-content">
                          {message.content}
                        </span>
                      </div>
                    </div>
                  </Link>
                </li>
              ))}
            </ul>
          </div>
          <div className="topnav-dropdown-footer">
            <Link href="/">See all messages</Link>
          </div>
        </div>
      </div>*/}
      <style jsx>{`
        @media only screen and (max-width: 768px) {
          .header-left {
            margin-left: -100px;
          }
        }
      `}</style>
    </div>
  );
};

export default Header;
