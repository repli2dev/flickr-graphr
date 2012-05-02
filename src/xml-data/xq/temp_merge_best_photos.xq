(: putting best photos together :)

<best-photos>
    {
    for $photo in doc("photos-src/best_photos_2012-04-30.xml")//photo |
                  doc("photos-src/best_photos_2012-04-29.xml")//photo |
                  doc("photos-src/best_photos_2012-04-28.xml")//photo |
                  doc("photos-src/best_photos_2012-04-27.xml")//photo |
                  doc("photos-src/best_photos_2012-04-26.xml")//photo |
                  doc("photos-src/best_photos_2012-04-25.xml")//photo |
                  doc("photos-src/best_photos_2012-04-24.xml")//photo |
                  doc("photos-src/best_photos_2012-04-23.xml")//photo |
                  doc("photos-src/best_photos_2012-04-22.xml")//photo |
                  doc("photos-src/best_photos_2012-04-21.xml")//photo |
                  doc("photos-src/best_photos_2012-04-20.xml")//photo |
                  doc("photos-src/best_photos_2012-04-19.xml")//photo |
                  doc("photos-src/best_photos_2012-04-18.xml")//photo |
                  doc("photos-src/best_photos_2012-04-17.xml")//photo
    return $photo
    }
</best-photos>