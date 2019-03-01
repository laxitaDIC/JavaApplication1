# Extract pitch from file (first parameter) and write it out to a text file
# (second parameter).

form PitchExtractor
sentence sound_file_name
sentence pitch_file_name
sentence Directory
endform

echo Reading from 'Directory$''sound_file_name$'
Read from file... 'Directory$''sound_file_name$'
To Pitch... 0.0 75 600

pitchID = selected("Pitch");
Down to PitchTier
pitchtierID = selected("PitchTier")
num_points = Get number of points

filedelete 'Directory$''pitch_file_name$'
echo Writing to 'Directory$''pitch_file_name$'
for i to num_points
time = Get time from index... i
hertz = Get value at index... i
fileappend "'Directory$''pitch_file_name$'" 'time' 'hertz' 'newline$'
endfor