import { Inter } from 'next/font/google';
import '../globals.css';
import {NextIntlClientProvider} from 'next-intl';
import {getMessages} from 'next-intl/server';
const inter = Inter({ subsets: ['latin'] });
import { cookies } from 'next/headers';


export const metadata = {
    title: "e-ESJ",
    description: "e-Espace Santé Jeunes",
  };
 

export default async function RootLayout({
  children,
 
}) {
    const cookieStore = cookies();
    const nextLocaleCookie = cookieStore.get('NEXT_LOCALE');
    const nextLocale = nextLocaleCookie ? nextLocaleCookie.value : 'fr';

    // Validate and sanitize the locale
    const locales = (nextLocale && typeof nextLocale === 'string' && nextLocale.trim())
      ? nextLocale.trim()
      : 'fr';

    // Ensure the locale is supported
    const validLocale = ['fr', 'ar'].includes(locales) ? locales : 'fr';

    const messages = await getMessages();

    return (
    <html lang={validLocale} dir={validLocale === 'ar' ? 'rtl' : 'ltr'}>
      <body className={inter.className}>
        <NextIntlClientProvider messages={messages}>
           {children}
        </NextIntlClientProvider>
      </body>
    </html>
  );
}