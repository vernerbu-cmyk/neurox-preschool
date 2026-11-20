from pathlib import Path
from io import BytesIO
from PIL import Image, ImageOps
import cairosvg
base = Path(__file__).resolve().parent
res = base / 'app/src/main/res'
svg = (base / 'icon.svg').read_bytes()
png = cairosvg.svg2png(bytestring=svg, output_width=1024, output_height=1024)
art = Image.open(BytesIO(png)).convert('RGBA')
sizes = {'mdpi':48,'hdpi':72,'xhdpi':96,'xxhdpi':144,'xxxhdpi':192}
for density,size in sizes.items():
  folder = res / ('mipmap-'+density)
  folder.mkdir(parents=True,exist_ok=True)
  img = art.resize((size,size),Image.Resampling.LANCZOS)
  img.save(folder/'ic_launcher.png',optimize=True)
  # Masked circular icon for older Android versions
  circle=Image.new('L',(size,size),0)
  from PIL import ImageDraw
  ImageDraw.Draw(circle).ellipse((0,0,size-1,size-1),fill=255)
  img.putalpha(circle)
  img.save(folder/'ic_launcher_round.png',optimize=True)
folder = res/'drawable-nodpi'
folder.mkdir(parents=True,exist_ok=True)
fg=Image.new('RGBA',(432,432),(0,0,0,0))
safe=art.resize((282,282),Image.Resampling.LANCZOS)
fg.alpha_composite(safe,((432-282)//2,(432-282)//2))
fg.save(folder/'ic_launcher_foreground.png',optimize=True)
folder=res/'mipmap-anydpi-v26'
folder.mkdir(parents=True,exist_ok=True)
xml='''<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
<background android:drawable="@color/launcher_background"/>
<foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>'''
for name in ('ic_launcher.xml','ic_launcher_round.xml'):
  (folder/name).write_text(xml,encoding='utf-8')
print('Generated launcher icons', flush=True)
