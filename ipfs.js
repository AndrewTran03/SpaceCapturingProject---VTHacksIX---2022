import * as IPFS from 'ipfs-core'
import * as fs from 'fs';

const ipfs = await IPFS.create()

let picture = {path:"C:/Users/mattb/Documents/VTHacks2022/VTHacks2022/2022-02-25.jpg"};

const { cid } = await ipfs.add(picture)
console.info(cid)

const content = ""+cid


fs.writeFile('C:/Users/mattb/Documents/VTHacks2022/VTHacks2022/test.txt', content, err => {
  if (err) {
    console.error(err)
    return
  }
  //file written successfully
})
