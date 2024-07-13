"use client";

import React, { useState, useEffect, useRef } from 'react';

import LogoJeune from './LogoJeune';

import { FaBars, FaRegUserCircle, FaChevronDown } from 'react-icons/fa';
import { PiSignOut } from "react-icons/pi";
import { IoCheckmark } from "react-icons/io5";
import { RxCross1 } from "react-icons/rx";
import { FaRegUser } from "react-icons/fa6";
import { FiChevronRight } from "react-icons/fi";


export default function Navbar() {
    
  const [isOpen, setIsOpen] = useState(false);
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [selectedTab, setSelectedTab] = useState('Accueil');
  const dropdownRef = useRef(null);
  const sidebarRef = useRef(null);

  const toggleSidebar = () => {
    setIsOpen(!isOpen);
  };

  const handleTabClick = (tab) => {
    setSelectedTab(tab);
    setIsOpen(false); // Close sidebar on tab selection in mobile view
  };

  const toggleDropdown = () => {
    setDropdownOpen(!dropdownOpen);
  };

  const handleClickOutside = (event) => {
    if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
      setDropdownOpen(false);
    }
    if (sidebarRef.current && !sidebarRef.current.contains(event.target) && isOpen) {
      setIsOpen(false);
    }
  };

  useEffect(() => {
    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [isOpen]);

  const menuItems = [
    { title: "Profile", icon: <FaRegUser /> },
    { title: "Français", icon: <IoCheckmark /> },
    { title: "العربية", icon: <RxCross1 /> },
    { title: "Se déconnecter", icon: <PiSignOut /> },
  ];

  const navItems = [
    { title: 'Accueil', href: '#' },
    { title: 'Dossier Medical', href: '#' },
    { title: 'Education à la Santé', href: '#' },
    { title: 'Tests Psychologiques', href: '#' },
  ];

  return (
    <nav className="p-4 shadow-gray-300 shadow-md h-16 flex items-center justify-between bg-white text-black relative">
      <div className="container mx-auto flex items-center justify-between">
        <LogoJeune height={60} width={120} className="lg:hidden mx-auto" />
        <div className="hidden lg:flex flex-grow justify-center space-x-12 font-medium">
          {navItems.map((item) => (
            <a
              key={item.title}
              href={item.href}
              className={`hover:font-semibold text-gray-950 ${selectedTab === item.title ? 'border-b-4 pb-2 border-blue-900 font-semibold' : ''}`}
              onClick={() => handleTabClick(item.title)}
            >
              {item.title}
            </a>
          ))}
        </div>
        <div className="hidden lg:flex items-center space-x-2 relative" ref={dropdownRef}>
          <button onClick={toggleDropdown} className="flex items-center space-x-2 text-blue-950">
            <FaRegUserCircle size={24} />
            <span>Ahmed M.</span>
            <FaChevronDown size={16} />
          </button>
          {dropdownOpen && (
            <div className="absolute -right-12 top-8 w-48 bg-gray-50 border border-gray-300 rounded-md shadow-lg z-10">
              <ul className="py-2">
                {menuItems.map((item) => (
                  <li key={item.title} className="px-4 py-2 hover:bg-gray-100 flex items-center space-x-2">
                    {item.icon}
                    <a href="#" className="block text-sm font-medium text-gray-700">
                      {item.title}
                    </a>
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>
        <button className="lg:hidden" onClick={toggleSidebar}>
          <FaBars size={24} />
        </button>
      </div>

      <div
        className={`fixed inset-0 bg-gray-800 bg-opacity-75 ${isOpen ? 'translate-x-0' : '-translate-x-full'} transition-transform duration-0 lg:hidden`}
      >
        <div ref={sidebarRef} className={`transform ${isOpen ? 'translate-x-0' : '-translate-x-full'} transition-transform duration-300 ease-in-out p-4 bg-white h-full flex flex-col justify-between w-3/5`}>
          <div>
            <div className="flex justify-between mb-4">
              <LogoJeune height={60} width={120} />
              <button onClick={toggleSidebar} className="text-black">
                <RxCross1 size={24} />
              </button>
            </div>
            <nav className="space-y-4">
              {navItems.map((item) => (
                <div key={item.title}>
                  <a
                    href={item.href}
                    className={`pb-2 flex justify-between items-center text-black hover:font-bold font-semibold transition-colors duration-300 ease-in-out ${selectedTab === item.title ? 'font-bold' : ''}`}
                    onClick={() => handleTabClick(item.title)}
                  >
                    {item.title}
                    <FiChevronRight />
                  </a>
                  <hr className="border-gray-300 " />
                </div>
              ))}
            </nav>
          </div>
          <nav className="space-y-4">
            {menuItems.map((item) => (
              <div key={item.title}>
                <a
                  href="#"
                  className="flex items-center space-x-2 text-black hover:font-semibold transition-colors duration-300 ease-in-out"
                >
                  {item.icon}
                  <span>{item.title}</span>
                </a>
                <hr className="border-gray-300" />
              </div>
            ))}
          </nav>
        </div>
      </div>
    </nav>
  );
}
