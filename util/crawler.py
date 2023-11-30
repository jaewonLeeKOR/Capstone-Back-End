from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.remote.webelement import WebElement
import clipboard
import time
import os.path
import sys

sys.stdout.reconfigure(encoding='utf-8')

def upload_image(key, image_path, chromedriverPath):
    chrome_options = webdriver.ChromeOptions()
    chrome_options.add_argument('--disable-features=ClipboardContentSettingApi')
    #chrome_options.add_argument("--headless");
    # linux 환경에서 필요한 option
    chrome_options.add_argument('--no-sandbox')
    chrome_options.add_argument('--disable-dev-shm-usage')

    driver = webdriver.Chrome(service=Service(r'{chromedriverPath}'), options=chrome_options)
    driver.get('https://makereal.tldraw.com/')
    driver.implicitly_wait(30)

    driver.maximize_window()

    try:
        key_input = WebDriverWait(driver, 30).until(
            EC.presence_of_element_located((By.XPATH, '//*[@id="openai_key_risky_but_cool"]'))
        )
        driver.implicitly_wait(30)
        key_input.send_keys(key)
        driver.implicitly_wait(10)
        key_input.send_keys(Keys.RETURN)

        #drop_zone = WebDriverWait(driver, 30).until(
        #    EC.presence_of_element_located((By.XPATH, '/html/body/div/div/span/div/div[1]'))
        #)

        drop_zone = driver.find_element(By.XPATH, "/html/body/div/div/span/div/div[1]")
        drop_zone.drop_files(image_path)

        time.sleep(5) # 파일 업로드를 기다립니다.

        #WebDriverWait(driver, 30).until(
        #    EC.presence_of_element_located((By.XPATH, '/html/body/div/div/div[2]/div[1]/div[3]/button'))
        #).click() # make real을 클릭합니다.

        driver.find_element(By.XPATH, "/html/body/div/div/div[2]/div[1]/div[3]/button").click()


        time.sleep(30) # make real 클릭 후 파일 생성을 기다립니다.

        #copy_button = WebDriverWait(driver, 30).until(
        #    EC.presence_of_element_located((By.CSS_SELECTOR, 'button[title="Copy code to clipboard"]'))
        #)

        copy_button = driver.find_element(By.CSS_SELECTOR, 'button[title="Copy code to clipboard"]')

        EC.element_to_be_clickable((By.CSS_SELECTOR, 'button[title="Copy code to clipboard"]'))

        copy_button.click()


        print(clipboard.paste())

    except Exception as e:
        print(e)
    #finally:
    #   driver.quit()


JS_DROP_FILES = "var c=arguments,b=c[0],k=c[1];c=c[2];for(var d=b.ownerDocument||document,l=0;;){var e=b.getBoundingClientRect(),g=e.left+(k||e.width/2),h=e.top+(c||e.height/2),f=d.elementFromPoint(g,h);if(f&&b.contains(f))break;if(1<++l)throw b=Error('Element not interactable'),b.code=15,b;b.scrollIntoView({behavior:'instant',block:'center',inline:'center'})}var a=d.createElement('INPUT');a.setAttribute('type','file');a.setAttribute('multiple','');a.setAttribute('style','position:fixed;z-index:2147483647;left:0;top:0;');a.onchange=function(b){a.parentElement.removeChild(a);b.stopPropagation();var c={constructor:DataTransfer,effectAllowed:'all',dropEffect:'none',types:['Files'],files:a.files,setData:function(){},getData:function(){},clearData:function(){},setDragImage:function(){}};window.DataTransferItemList&&(c.items=Object.setPrototypeOf(Array.prototype.map.call(a.files,function(a){return{constructor:DataTransferItem,kind:'file',type:a.type,getAsFile:function(){return a},getAsString:function(b){var c=new FileReader;c.onload=function(a){b(a.target.result)};c.readAsText(a)}}}),{constructor:DataTransferItemList,add:function(){},clear:function(){},remove:function(){}}));['dragenter','dragover','drop'].forEach(function(a){var b=d.createEvent('DragEvent');b.initMouseEvent(a,!0,!0,d.defaultView,0,0,0,g,h,!1,!1,!1,!1,0,null);Object.setPrototypeOf(b,null);b.dataTransfer=c;Object.setPrototypeOf(b,DragEvent.prototype);f.dispatchEvent(b)})};d.documentElement.appendChild(a);a.getBoundingClientRect();return a;"

def drop_files(element, files, offsetX=0, offsetY=0):
    driver = element.parent
    isLocal = not driver._is_remote or '127.0.0.1' in driver.command_executor._url
    paths = []

    # ensure files are present, and upload to the remote server if session is remote
    for file in (files if isinstance(files, list) else [files]) :
        if not os.path.isfile(file) :
            raise FileNotFoundError(file)
        paths.append(file if isLocal else element._upload(file))

    value = '\n'.join(paths)
    elm_input = driver.execute_script(JS_DROP_FILES, element, offsetX, offsetY)
    elm_input._execute('sendKeysToElement', {'value': [value], 'text': value})

WebElement.drop_files = drop_files

key = sys.argv[1]
image_path =sys.argv[2]


upload_image(key, image_path)
