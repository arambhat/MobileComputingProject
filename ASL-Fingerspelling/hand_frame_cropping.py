import glob
import cv2
import math
import mediapipe as mp
import pandas as pd
import os
import numpy as np

def calc_bounding_rect(image, landmarks):
        image_width, image_height = image.shape[1], image.shape[0]
        landmark_array = np.empty((0, 2), int)
        for _, landmark in enumerate(landmarks.landmark):
            landmark_x = min(int(landmark.x * image_width), image_width - 1)
            landmark_y = min(int(landmark.y * image_height), image_height - 1)

            landmark_point = [np.array((landmark_x, landmark_y))]

            landmark_array = np.append(landmark_array, landmark_point, axis=0)

        x, y, w, h = cv2.boundingRect(landmark_array)

        return [x-20, y-20, x + w+20, y + h+20]

def crop_hand_frame_alphabets(frame_folder, hand_frames_folder):
    print("Extracting palm from the extracted frames.....")
    frames =  [file for file in os.listdir(frame_folder) if file.endswith('.png')]
    files = sorted(frames,key=lambda x: int(os.path.splitext(x)[0]))
    mp_hands = mp.solutions.hands
    hands = mp_hands.Hands(
        static_image_mode= True,
        max_num_hands=1,
        min_detection_confidence=0.7,
    )
    if not os.path.isdir(hand_frames_folder):
        os.mkdir(hand_frames_folder)
    for i, video_frame in enumerate(files):
        # print(i)
        # print(video_frame)
        image_path = os.path.join(frame_folder, video_frame)
        img = cv2.imread(image_path)
        results = hands.process(cv2.cvtColor(img, cv2.COLOR_BGR2RGB))
        # print(results.multi_hand_landmarks)
        if results.multi_hand_landmarks is not None:
            for hand_landmarks, handedness in zip(results.multi_hand_landmarks,
                                                    results.multi_handedness):
                brect = calc_bounding_rect(img, hand_landmarks)
                # print("brect:" + str(brect))
                cropped_image = img[brect[1]:brect[3] , brect[0]:brect[2]]
                image_path = os.path.join(hand_frames_folder, str(i)+".png")
                #cv2.rectangle(img, (brect[0], brect[1]), (brect[2], brect[3]), (0, 0, 255), 1)
                cv2.imwrite(image_path,cropped_image)



def crop_hand_frame_words(frames_folder_path, hand_frames_folder_path):
    print("Cropping handshape from extracted video frames .....")
    pos_key = pd.read_csv(os.path.join(frames_folder_path, 'key_points.csv'))
    rightWrist_x = pos_key.rightWrist_x
    rightWrist_y = pos_key.rightWrist_y
    leftWrist_x = pos_key.leftWrist_x
    leftWrist_y = pos_key.leftWrist_y

    print("rx"+str(rightWrist_x))
    print("ry"+str(rightWrist_y))
    print("lx"+str(leftWrist_x))
    print("ly"+str(leftWrist_y))

    frames =  [file for file in os.listdir(frames_folder_path) if file.endswith('.png')]
    files = sorted(frames,key=lambda x: int(os.path.splitext(x)[0]))
    # print("---------files-------------")
    # print(files)
    i = 0

    if not os.path.isdir(hand_frames_folder_path):
        os.mkdir(hand_frames_folder_path)

    for video_frame in files:
        print("---------frame " + str(i) + " processing-------------")
        print(len(rightWrist_x))
        print(i)
        # try:
        if i< len(rightWrist_x):
            image_path = os.path.join(frames_folder_path, video_frame)
            img = cv2.imread(image_path)
            print(img.shape)

            if int(rightWrist_y[i]-700)<0:
                y=0
            else:
                y=int(rightWrist_y[i]-700)
            if int(rightWrist_y[i]+100)>1440:
                ymax=1440
            else:
                ymax=int(rightWrist_y[i]+100)
            if int(rightWrist_x[i]-300)<0:
                x=0
            else:
                x=int(rightWrist_x[i]-300)
            if int(rightWrist_x[i]+300)>1440:
                xmax=1440
            else:
                xmax=int(rightWrist_x[i]+300)


            cropped_image = img[y:ymax , x:xmax]
            #print("---------cropped image-------------")
            #print(cropped_image)
            #flipped_cropped_image = cv2.flip(cropped_image,1)
            image_path = os.path.join(hand_frames_folder_path, str(i) + ".png")
            #cv2.imwrite(image_path,flipped_cropped_image)
            cv2.imwrite(image_path,cropped_image)
            i = i + 1
        # except :
            # i = i + 1
