param([string]$out, [int]$S = 512)
Add-Type -AssemblyName System.Drawing
$bmp = New-Object System.Drawing.Bitmap $S, $S
$g = [System.Drawing.Graphics]::FromImage($bmp)
$g.SmoothingMode = 'AntiAlias'
$g.Clear([System.Drawing.Color]::Transparent)
$k = $S / 512.0
function P($x, $y) { New-Object System.Drawing.PointF ([float]($x * $k)), ([float]($y * $k)) }

# One UI squircle: superellipse |x|^n + |y|^n = 1, n = 5 (same shape as the guide's icons)
$pts = New-Object System.Collections.Generic.List[System.Drawing.PointF]
$n = 5.0; $r = $S / 2 - 4 * $k; $c = $S / 2
for ($i = 0; $i -lt 720; $i++) {
  $t = 2 * [Math]::PI * $i / 720
  $ct = [Math]::Cos($t); $st = [Math]::Sin($t)
  $x = [Math]::Sign($ct) * [Math]::Pow([Math]::Abs($ct), 2 / $n)
  $y = [Math]::Sign($st) * [Math]::Pow([Math]::Abs($st), 2 / $n)
  $pts.Add((New-Object System.Drawing.PointF ([float]($c + $r * $x)), ([float]($c + $r * $y))))
}
$path = New-Object System.Drawing.Drawing2D.GraphicsPath
$path.AddPolygon($pts.ToArray())
$rect = New-Object System.Drawing.RectangleF 0, 0, $S, $S
$top = [System.Drawing.Color]::FromArgb(255, 255, 200, 40)
$bot = [System.Drawing.Color]::FromArgb(255, 245, 160, 0)
$g.FillPath((New-Object System.Drawing.Drawing2D.LinearGradientBrush $rect, $top, $bot, 90), $path)

# White outline screw-in bulb: round glass narrowing to the neck, threaded base, rays all around the glass
$pen = New-Object System.Drawing.Pen ([System.Drawing.Color]::White), ([float](22 * $k))
$pen.LineJoin = 'Round'; $pen.StartCap = 'Round'; $pen.EndCap = 'Round'
$bulb = New-Object System.Drawing.Drawing2D.GraphicsPath
# Glass: arc of a circle centred (256,220) r 92, from the left neck point round the top to the right neck point
$bulb.AddArc([float](164 * $k), [float](128 * $k), [float](184 * $k), [float](184 * $k), 145, 250)
$bulb.AddLine((P 331 273), (P 296 318))
$bulb.AddLine((P 296 318), (P 296 340))
$bulb.AddLine((P 296 340), (P 216 340))
$bulb.AddLine((P 216 340), (P 216 318))
$bulb.CloseFigure()
$g.DrawPath($pen, $bulb)
# Threaded base and contact tip
$g.DrawLine($pen, (P 220 372), (P 292 372))
$g.DrawLine($pen, (P 220 402), (P 292 402))
$g.DrawLine($pen, (P 236 430), (P 276 430))

# Rays around the glass
foreach ($a in @(-180, -144, -108, -72, -36, 0)) {
  $rad = $a * [Math]::PI / 180
  $x1 = 256 + 128 * [Math]::Cos($rad); $y1 = 220 + 128 * [Math]::Sin($rad)
  $x2 = 256 + 176 * [Math]::Cos($rad); $y2 = 220 + 176 * [Math]::Sin($rad)
  $g.DrawLine($pen, (P $x1 $y1), (P $x2 $y2))
}

$bmp.Save($out, [System.Drawing.Imaging.ImageFormat]::Png)
$g.Dispose(); $bmp.Dispose()
