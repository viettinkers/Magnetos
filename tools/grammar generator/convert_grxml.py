fRead = open('vegetables.txt', 'r')
fWrite = open('vegetables.grxml', 'w')

allVegies = fRead.read().splitlines();

for line in allVegies:
	fWrite.write('        <item> ' + line.lower() + ' </item>\n')

fRead.close()
fWrite.close()